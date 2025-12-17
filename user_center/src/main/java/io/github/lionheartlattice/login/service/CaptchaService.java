package io.github.lionheartlattice.login.service;

import cn.hutool.core.util.IdUtil;
import com.easy.query.core.proxy.core.draft.Draft1;
import com.easy.query.core.proxy.sql.Select;
import io.github.lionheartlattice.configuration.s3bult.OssService;
import io.github.lionheartlattice.entity.parent.ZFile;
import io.github.lionheartlattice.entity.user_center.vo.ChallengeInfo;
import io.github.lionheartlattice.util.AESUtil;
import io.github.lionheartlattice.util.CaptchaImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

/**
 * 验证码服务类
 * 负责验证码的生成、验证等操作
 *
 * @author lionheart
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final RedissonClient redissonClient;
    private final OssService ossService;

    @Value("${app.captcha.expire-minutes:5}")
    private int expireMinutes;

    @Value("${app.captcha.tolerance:10}")
    private int tolerance;

    @Value("${app.captcha.watermark-text:}")
    private String watermarkText;

    /**
     * 创建认证挑战（包含滑块验证码和临时密钥）
     *
     * @param clientId 客户端ID
     * @return 挑战信息
     */
    public ChallengeInfo createChallenge(String clientId) {
        // 1. 生成唯一请求ID
        String requestId = IdUtil.fastSimpleUUID();

        // 2. 生成16位随机字符串作为临时AES密钥
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
                // 生成验证码（传入水印文字）
                captchaImage = CaptchaImageUtil.generate(in, watermarkText);
            }
        } catch (Exception e) {
            log.error("生成验证码失败", e);
            throw new RuntimeException("生成验证码失败");
        }

        // 4. 存入 Redis，设置过期时间
        String key = "challenge:" + requestId;
        redissonClient.getBucket(key)
                      .set(secretKey, Duration.ofMinutes(expireMinutes));

        // 存入验证码坐标
        String captchaKey = "captcha:" + requestId;
        redissonClient.getBucket(captchaKey)
                      .set(captchaImage.getX(), Duration.ofMinutes(expireMinutes));

        // 5. 构建返回对象
        return new ChallengeInfo().setRequestId(requestId)
                                  .setSecretKey(secretKey)
                                  .setBackgroundImage(removeBase64Prefix(captchaImage.getBackgroundImage()))
                                  .setSliderImage(removeBase64Prefix(captchaImage.getSliderImage()))
                                  .setY(captchaImage.getY());
    }

    /**
     * 验证滑块验证码
     *
     * @param requestId 请求ID
     * @param moveX     前端传递的滑动距离
     * @throws RuntimeException 验证失败时抛出异常
     */
    public void verifyCaptcha(String requestId, Integer moveX) {
        String captchaKey = "captcha:" + requestId;
        Object storedXObj = redissonClient.getBucket(captchaKey)
                                          .get();

        if (storedXObj == null) {
            throw new RuntimeException("验证码已过期或无效");
        }

        int storedX = (Integer) storedXObj;

        // 校验移动距离（允许容错值）
        if (moveX == null || Math.abs(storedX - moveX) > tolerance) {
            log.error("验证码验证失败: storedX={}, moveX={}", storedX, moveX);
            // 删除验证码，防止重复尝试
            redissonClient.getBucket(captchaKey)
                          .delete();
            throw new RuntimeException("验证码验证失败");
        }

        // 验证通过后删除验证码（一次性使用）
        redissonClient.getBucket(captchaKey)
                      .delete();
        log.info("验证码校验成功，requestId: {}", requestId);
    }

    /**
     * 获取临时密钥（用于解密前端加密的密码）
     *
     * @param requestId 请求ID
     * @return 临时密钥
     */
    public String getSecretKey(String requestId) {
        String challengeKey = "challenge:" + requestId;
        Object secretKeyObj = redissonClient.getBucket(challengeKey)
                                            .get();

        if (secretKeyObj == null) {
            throw new RuntimeException("登录请求已过期，请刷新重试");
        }

        return (String) secretKeyObj;
    }

    /**
     * 使用 AES-GCM 模式解密数据
     *
     * @param encryptedData 加密后的数据(Base64编码)
     * @param secretKey     密钥
     * @param iv            初始化向量(Base64编码)
     * @return 解密后的明文
     */
    public String decryptPassword(String encryptedData, String secretKey, String iv) {
        try {
            return AESUtil.aesDecrypt(encryptedData, secretKey, iv);
        } catch (Exception e) {
            log.error("密码解密失败", e);
            throw new RuntimeException("密码解密失败");
        }
    }

    /**
     * 获取验证码背景图 URL
     *
     * @return 背景图 URL
     */
    private String getBackgroundForCaptcha() {
        Draft1<String> picKey = new ZFile().queryable()
                                           .where(z -> z.usage()
                                                        .eq("backgroundForCaptcha"))
                                           .orderBy(z -> z.expression()
                                                          .rawSQLStatement("RANDOM()")
                                                          .asc())
                                           .select(z -> Select.DRAFT.of(z.id()))
                                           .firstNotNull();

        return ossService.getPublicUrl(picKey.getValue1());
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
}

