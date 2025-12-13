package io.github.lionheartlattice.user_center.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.easy.query.core.proxy.core.draft.Draft2;
import com.easy.query.core.proxy.sql.Select;
import io.github.lionheartlattice.entity.user_center.dto.LoginDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.entity.user_center.po.proxy.RoleProxy;
import io.github.lionheartlattice.entity.user_center.po.proxy.UserProxy;
import io.github.lionheartlattice.util.response.ErrorEnum;
import io.github.lionheartlattice.util.response.ExceptionWithEnum;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final RedissonClient redissonClient;
    @Value("${app.auth.token-key-prefix:Bearer_}")
    private String tokenKeyPrefix;
    @Value("${app.auth.token-ttl-seconds:604800}")
    private Long tokenTtlSeconds;

    @Value("${app.auth.aes-key:LionHeartLattice}")
    private String aesKey;

    private AES aes;

    @PostConstruct
    public void init() {
        // 初始化AES工具
        this.aes = SecureUtil.aes(aesKey.getBytes(StandardCharsets.UTF_8));
    }

    public User detailWithInclude(BigInteger id) {
        return new User().queryable()
                         .include(UserProxy::deptList)
                         .include(UserProxy::roleList, r -> r.include(RoleProxy::menuList))
                         .whereById(id)
                         .singleNotNull();
    }

    public String login(LoginDTO dto) {
        // 查询用户ID和加密后的密码
        // 使用 singleOrNull 避免用户不存在时抛出特定异常，统一处理为用户名或密码错误
        Draft2<BigInteger, String> draft2 = new User().queryable()
                                                      .where(u -> u.username()
                                                                   .eq(dto.getUsername()))
                                                      .select(u -> Select.DRAFT.of(u.id(), u.pwd()))
                                                      .singleOrNull();

        if (draft2 == null) {
            throw new ExceptionWithEnum(ErrorEnum.BAD_USERNAME_OR_PASSWORD);
        }

        // 使用配置的AES密钥加密输入的密码，然后与数据库中的密文比对
        // 修正：前端传递的字段为 pwd，对应 DTO 的 getPwd() 方法，原 getPassword() 因字段不匹配导致为 null
        String inputPwdEncrypted = aes.encryptHex(dto.getPwd());

        if (!inputPwdEncrypted.equals(draft2.getValue2())) {
            throw new ExceptionWithEnum(ErrorEnum.BAD_USERNAME_OR_PASSWORD);
        }
        return createToken(draft2.getValue1());
    }

    /**
     * 创建 token 并写入 Redis
     *
     * @param userId 用户ID
     * @return token 字符串
     */
    public String createToken(BigInteger userId) {
        // Hutool：fastUUID() 无 '-'，更短更适合当 token
        String token = IdUtil.fastUUID();
        String key = tokenKeyPrefix + token;

        redissonClient.getBucket(key)
                      .set(detailWithInclude(userId), tokenTtlSeconds, TimeUnit.SECONDS);

        return token;
    }

    /**
     * 根据 token 获取 User（用于鉴权）
     */
    public User getUserByToken(String token) {
        Object value = redissonClient.getBucket(tokenKeyPrefix + token)
                                     .get();
        return (User) value;
    }

    /**
     * 退出登录：删除 token
     */
    public boolean revokeToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return redissonClient.getBucket(tokenKeyPrefix + token)
                             .delete();
    }

    private String resolveToken(HttpServletRequest request) {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(auth) && auth.startsWith(tokenKeyPrefix)) {
            return auth.substring(tokenKeyPrefix.length())
                       .trim();
        } else {
            throw new ExceptionWithEnum(ErrorEnum.BAD_USERNAME_OR_PASSWORD);
        }
    }

    /**
     * 登出：删除 token
     *
     * @param token token 字符串
     * @return 删除是否成功
     */
    public boolean logout(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        boolean deleted = redissonClient.getBucket(tokenKeyPrefix + token)
                                        .delete();
        if (deleted) {
            log.info("Token revoked: {}", token);
        }
        return deleted;
    }
}
