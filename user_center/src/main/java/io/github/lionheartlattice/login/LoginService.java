package io.github.lionheartlattice.login;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.easy.query.core.proxy.core.draft.Draft2;
import com.easy.query.core.proxy.sql.Select;
import io.github.lionheartlattice.entity.user_center.user.dto.LoginDTO;
import io.github.lionheartlattice.entity.user_center.user.po.Menu;
import io.github.lionheartlattice.entity.user_center.user.po.User;
import io.github.lionheartlattice.entity.user_center.po.proxy.RoleProxy;
import io.github.lionheartlattice.entity.user_center.po.proxy.UserProxy;
import io.github.lionheartlattice.entity.user_center.user.vo.UserWithMenu;
import io.github.lionheartlattice.util.CopyUtil;
import io.github.lionheartlattice.configuration.exception.ErrorEnum;
import io.github.lionheartlattice.configuration.exception.ExceptionWithEnum;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final RedissonClient redissonClient;
    private final CaptchaService captchaService;
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

    public UserWithMenu login(LoginDTO dto) {
        // 0. 校验验证码
        captchaService.verifyCaptcha(dto.getRequestId(), dto.getMoveX());

        // 1. 解密前端传来的密码
        String secretKey = captchaService.getSecretKey(dto.getRequestId());
        String rawPassword = captchaService.decryptPassword(dto.getPassword(), secretKey, dto.getIv());

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
        return userWithMenu.setAccessToken(token);
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
}
