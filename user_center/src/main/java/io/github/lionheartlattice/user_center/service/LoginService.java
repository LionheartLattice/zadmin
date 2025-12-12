package io.github.lionheartlattice.user_center.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.easy.query.core.proxy.core.draft.Draft2;
import com.easy.query.core.proxy.sql.Select;
import io.github.lionheartlattice.entity.user_center.dto.LoginDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.entity.user_center.po.proxy.RoleProxy;
import io.github.lionheartlattice.entity.user_center.po.proxy.UserProxy;
import io.github.lionheartlattice.util.response.ErrorEnum;
import io.github.lionheartlattice.util.response.ExceptionWithEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private static final String TOKEN_KEY_PREFIX = "Bearer:";
    private final RedissonClient redissonClient;
    /**
     * token 过期时间（秒）
     * 默认 7 天：7*24*3600
     */
    @Value("${app.auth.token-ttl-seconds:604800}")
    private long tokenTtlSeconds;

    public User detailWithInclude(Long id) {
        return new User().queryable()
                         .include(UserProxy::deptList)
                         .include(UserProxy::roleList, r -> r.include(RoleProxy::menuList))
                         .whereById(id)
                         .singleNotNull();
    }

    public String login(LoginDTO dto) {
        Draft2<Long, String> draft2 = new User().queryable()
                                                .where(u -> u.username()
                                                             .eq(dto.getUsername()))
                                                .select(u -> Select.DRAFT.of(u.id(), u.pwd()))
                                                .singleNotNull();
        boolean checkpwed = BCrypt.checkpw(dto.getPassword(), draft2.getValue2());
        if (!checkpwed) {
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
    public String createToken(Long userId) {
        // Hutool：fastUUID() 无 '-'，更短更适合当 token
        String token = IdUtil.fastUUID();
        String key = TOKEN_KEY_PREFIX + token;

        redissonClient.getBucket(key)
                      .set(detailWithInclude(userId), tokenTtlSeconds, TimeUnit.SECONDS);

        return token;
    }

    /**
     * 根据 token 获取 User（用于鉴权）
     */
    public User getUserByToken(String token) {
        Object value = redissonClient.getBucket(TOKEN_KEY_PREFIX + token)
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
        return redissonClient.getBucket(TOKEN_KEY_PREFIX + token)
                             .delete();
    }

    private String resolveToken(HttpServletRequest request) {
        // 1) 优先标准 Authorization: Bearer:<token>
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer:")) {
            return auth.substring("Bearer:".length())
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
        boolean deleted = redissonClient.getBucket(TOKEN_KEY_PREFIX + token).delete();
        if (deleted) {
            log.info("Token revoked: {}", token);
        }
        return deleted;
    }
}
