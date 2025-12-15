package io.github.lionheartlattice.user_center.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.easy.query.core.proxy.core.draft.Draft2;
import com.easy.query.core.proxy.sql.Select;
import io.github.lionheartlattice.configuration.s3bult.ZFileService;
import io.github.lionheartlattice.entity.parent.ZFile;
import io.github.lionheartlattice.entity.user_center.dto.LoginDTO;
import io.github.lionheartlattice.entity.user_center.po.Menu;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.entity.user_center.po.proxy.RoleProxy;
import io.github.lionheartlattice.entity.user_center.po.proxy.UserProxy;
import io.github.lionheartlattice.entity.user_center.vo.ChallengeInfo;
import io.github.lionheartlattice.entity.user_center.vo.LoginResultVO;
import io.github.lionheartlattice.entity.user_center.vo.UserWithMenu;
import io.github.lionheartlattice.util.CaptchaImageUtil;
import io.github.lionheartlattice.util.CopyUtil;
import io.github.lionheartlattice.util.response.ErrorEnum;
import io.github.lionheartlattice.util.response.ExceptionWithEnum;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final RedissonClient redissonClient;
    private final ZFileService zFileService;
    @Value("${app.auth.token-key-prefix:Bearer_}")
    private String tokenKeyPrefix;
    @Value("${app.auth.token-ttl-seconds:604800}")
    private long tokenTtlSeconds;

    @Value("${app.auth.aes-key:LionHeartLattice}")
    private String aesKey;

    @Value("${app.auth.vite_app_client-id}")
    private List<String> viteAppClientId;

    private AES aes;

    @PostConstruct
    public void init() {
        // 初始化AES工具
        this.aes = SecureUtil.aes(aesKey.getBytes(StandardCharsets.UTF_8));
    }


    public UserWithMenu detailWithInclude(BigDecimal id) {
        User user = new User().queryable()
                              .include(UserProxy::tenant)
                              .include(UserProxy::deptList)
                              .include(UserProxy::roleList, r -> r.include(RoleProxy::menuList))
                              .whereById(id)
                              .singleNotNull()
                              .setPwd(null);

        // 1. 收集所有菜单，去重并按 sort 排序
        List<Menu> allMenus = user.getRoleList()
                                  .stream()
                                  .filter(role -> role.getMenuList() != null)
                                  .flatMap(role -> role.getMenuList()
                                                       .stream())
                                  .distinct()
                                  .sorted(Comparator.comparing(Menu::getSort, Comparator.nullsLast(Integer::compareTo))
                                                    .thenComparing(Menu::getId))
                                  .toList();

        // 2. 组装树形结构
        List<Menu> treeMenus = new ArrayList<>();
        // 将菜单放入 Map 中以便快速查找
        Map<BigDecimal, Menu> menuMap = allMenus.stream()
                                                .collect(Collectors.toMap(Menu::getId, menu -> menu, (k1, k2) -> k1));

        for (Menu menu : allMenus) {
            // 确保 children 列表已初始化
            if (menu.getChildren() == null) {
                menu.setChildren(new ArrayList<>());
            }

            BigDecimal pid = menu.getPid();
            // 如果是顶级节点（pid为null或0），或者父节点不在当前权限列表中，则作为根节点
            if (pid == null || BigDecimal.ZERO.equals(pid) || !menuMap.containsKey(pid)) {
                treeMenus.add(menu);
            } else {
                // 如果有父节点，添加到父节点的 children 中
                Menu parent = menuMap.get(pid);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren()
                          .add(menu);
                }
            }
        }

        UserWithMenu userWithMenu = CopyUtil.copy(user, new UserWithMenu());
        return userWithMenu.setMenuList(treeMenus);
    }

    public LoginResultVO login(LoginDTO dto) {
        // 0. 校验验证码
        String captchaKey = "captcha:" + dto.getRequestId();
        Object storedXObj = redissonClient.getBucket(captchaKey).get();
        if (storedXObj == null) {
            throw new RuntimeException("验证码已过期或无效");
        }
        int storedX = (Integer) storedXObj;

        // 增加容错值到 10
        if (dto.getMoveX() == null || Math.abs(storedX - dto.getMoveX()) > 10) {
            log.error("验证码验证失败: storedX={}, moveX={}", storedX, dto.getMoveX());
            throw new RuntimeException("验证码验证失败");
        }
        // 验证通过后删除key，防止重放
        redissonClient.getBucket(captchaKey).delete();

        // 1. 解密前端传来的密码
        // 获取临时密钥
        String challengeKey = "challenge:" + dto.getRequestId();
        Object secretKeyObj = redissonClient.getBucket(challengeKey).get();
        if (secretKeyObj == null) {
            throw new RuntimeException("登录请求已过期，请刷新重试");
        }
        String secretKey = (String) secretKeyObj;

        String rawPassword;
        try {
            if (dto.getIv() != null) {
                // 前端使用 AES-GCM 模式加密，需要使用 Java 标准库解密
                rawPassword = decryptAesGcm(dto.getPassword(), secretKey, dto.getIv());
            } else {
                // 兼容旧版本，使用默认模式
                AES tempAes = SecureUtil.aes(secretKey.getBytes(StandardCharsets.UTF_8));
                rawPassword = tempAes.decryptStr(dto.getPassword());
            }
        } catch (Exception e) {
            log.error("密码解密失败", e);
            throw new RuntimeException("密码解密失败");
        }

        // 2. 数据库查询
        Draft2<BigDecimal, String> draft2 = new User().queryable()
                                                      .where(u -> u.username()
                                                                   .eq(dto.getUsername()))
                                                      .select(u -> Select.DRAFT.of(u.id(), u.pwd()))
                                                      .singleOrNull();

        if (draft2 == null) {
            throw new ExceptionWithEnum(ErrorEnum.BAD_USERNAME_OR_PASSWORD);
        }

        // 3. 使用配置的AES密钥加密解密后的原始密码，然后与数据库中的密文比对
        String inputPwdEncrypted = aes.encryptHex(rawPassword);

        if (!inputPwdEncrypted.equals(draft2.getValue2())) {
            throw new ExceptionWithEnum(ErrorEnum.BAD_USERNAME_OR_PASSWORD);
        }

        // 4. 生成 Token 并构建返回结果
        String token = createToken(draft2.getValue1());
        UserWithMenu userWithMenu = detailWithInclude(draft2.getValue1());

        return new LoginResultVO()
                .setAccessToken(token)
                .setUserInfo(userWithMenu)
                .setName(userWithMenu.getNickname())
                .setAvatar(userWithMenu.getLogo())
                .setIntroduction("Welcome")
                .setRoles(new ArrayList<>())
                .setPermissions(new ArrayList<>());
    }

    /**
     * 创建 token 并写入 Redis
     *
     * @param userId 用户ID
     * @return token 字符串
     */
    public String createToken(BigDecimal userId) {
        // 生成更长的 token 以降低碰撞概率 (双重UUID拼接，128字符)
        String token =
                IdUtil.fastSimpleUUID() + IdUtil.fastSimpleUUID() + IdUtil.fastSimpleUUID() + IdUtil.fastSimpleUUID();
        String key = tokenKeyPrefix + token;

        // 使用 Duration 替代过期的 TimeUnit 参数
        redissonClient.getBucket(key)
                      .set(detailWithInclude(userId), java.time.Duration.ofSeconds(tokenTtlSeconds));

        return token;
    }

    /**
     * 根据 token 获取 User（用于鉴权）
     */
    public UserWithMenu getUserByToken(String token) {
        Object value = redissonClient.getBucket(tokenKeyPrefix + token)
                                     .get();
        return (UserWithMenu) value;
    }


    /**
     * 登出：删除 token
     *
     * @param token token 字符串
     * @return 删除是否成功
     */
    public boolean logout(String token) {
        boolean deleted = redissonClient.getBucket(tokenKeyPrefix + token)
                                        .delete();
        if (deleted) {
            log.info("Token revoked: {}", token);
        }
        return deleted;
    }

    /**
     * 生成登录认证挑战信息（一次性RequestId和临时密钥）
     *
     * @return ChallengeInfo
     */
    public ChallengeInfo createChallenge(String clientId) {
        if (!viteAppClientId.contains(clientId)) {
            throw new RuntimeException("客户端未授信");
        }
        // 1. 生成唯一请求ID
        String requestId = IdUtil.fastSimpleUUID();
        // 2. 生成16位随机字符串作为临时AES密钥 (使用nanoId替代randomString)
        String secretKey = IdUtil.nanoId(16);

        // 3. 生成验证码
        String imageUrl = getBackgroundForCaptcha();
        if (imageUrl == null) {
            throw new RuntimeException("未找到验证码背景图片");
        }

        CaptchaImageUtil.CaptchaImage captchaImage;
        try {
            URL url = URI.create(imageUrl)
                         .toURL();
            try (InputStream in = url.openStream()) {
                captchaImage = CaptchaImageUtil.generate(in);
            }
        } catch (Exception e) {
            log.error("生成验证码失败", e);
            throw new RuntimeException("生成验证码失败");
        }

        // 4. 存入 Redis，设置过期时间为5分钟
        String key = "challenge:" + requestId;
        redissonClient.getBucket(key)
                      .set(secretKey, Duration.ofMinutes(5));

        // 存入验证码坐标
        String captchaKey = "captcha:" + requestId;
        redissonClient.getBucket(captchaKey)
                      .set(captchaImage.getX(), Duration.ofMinutes(5));

        // 5. 使用链式调用构建对象
        // 前端组件会自动拼接 data:image/png;base64, 前缀，所以这里需要移除后端生成的完整 Data URI 前缀
        return new ChallengeInfo().setRequestId(requestId)
                                  .setSecretKey(secretKey)
                                  .setBackgroundImage(removeBase64Prefix(captchaImage.getBackgroundImage()))
                                  .setSliderImage(removeBase64Prefix(captchaImage.getSliderImage()))
                                  .setY(captchaImage.getY());
    }

    /**
     * 去除 Base64 字符串的前缀 (data:image/xxx;base64,)
     */
    private String removeBase64Prefix(String base64) {
        if (base64 != null && base64.contains(",")) {
            return base64.substring(base64.indexOf(",") + 1);
        }
        return base64;
    }

    /**
     * 获取验证码背景图 URL
     *
     * @return 背景图 URL
     */
    public String getBackgroundForCaptcha() {
        // 优化：直接在数据库中使用 RANDOM() 进行排序并取第一条
        // 避免将所有符合条件的 ID 加载到内存中
        Draft2<BigDecimal, String> idAndExtension = new ZFile().queryable()
                                                               .where(z -> z.usage()
                                                                            .eq("backgroundForCaptcha"))
                                                               .orderBy(z -> z.expression()
                                                                              .rawSQLStatement("RANDOM()")
                                                                              .asc())
                                                               .select(z -> Select.DRAFT.of(z.id(), z.extension()))
                                                               .firstNotNull();


        return zFileService.getUrlByIdAndExtension(idAndExtension.getValue1(), idAndExtension.getValue2());
    }

    /**
     * 使用 AES-GCM 模式解密数据
     *
     * @param encryptedDataBase64 加密后的数据(Base64编码，包含密文和tag)
     * @param secretKey           密钥
     * @param ivBase64            初始化向量(Base64编码)
     * @return 解密后的明文
     */
    private String decryptAesGcm(String encryptedDataBase64, String secretKey, String ivBase64) throws Exception {
        // 解码 Base64
        byte[] encryptedDataWithTag = Base64.getDecoder().decode(encryptedDataBase64);
        byte[] iv = Base64.getDecoder().decode(ivBase64);

        // GCM 模式的 tag 长度是 16 字节 (128 位)
        int tagLength = 16;
        int ciphertextLength = encryptedDataWithTag.length - tagLength;

        // 分离密文和 tag
        byte[] ciphertext = new byte[ciphertextLength];
        byte[] tag = new byte[tagLength];
        System.arraycopy(encryptedDataWithTag, 0, ciphertext, 0, ciphertextLength);
        System.arraycopy(encryptedDataWithTag, ciphertextLength, tag, 0, tagLength);

        // 重新组合：GCM 解密需要密文+tag
        byte[] combined = new byte[encryptedDataWithTag.length];
        System.arraycopy(ciphertext, 0, combined, 0, ciphertextLength);
        System.arraycopy(tag, 0, combined, ciphertextLength, tagLength);

        // 创建密钥
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");

        // 创建 Cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

        // 解密
        byte[] decrypted = cipher.doFinal(combined);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
