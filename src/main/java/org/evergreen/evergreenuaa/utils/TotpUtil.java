package org.evergreen.evergreenuaa.utils;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * TOTP工具类，生成固定时间内的验证码
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TotpUtil {
    private static final long TIME_STEP = 6 * 5L;
    private static final int PASSWORD_LENGTH = 6;

    /**
     * Key生成器
     */
    private KeyGenerator keyGenerator;

    private TimeBasedOneTimePasswordGenerator timeBasedOneTimePasswordGenerator;

    /**
     * 初始化代码块
     */
    {
        try {
            timeBasedOneTimePasswordGenerator= new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(TIME_STEP), PASSWORD_LENGTH);
            keyGenerator = KeyGenerator.getInstance(timeBasedOneTimePasswordGenerator.getAlgorithm());
            keyGenerator.init(512);
        } catch (NoSuchAlgorithmException e) {
            log.error("没有找到算法: {}", e.getLocalizedMessage());
        }
    }

    /**
     *
     * @param key the key to be used to generate the password timestamp
     * @param time 用于生成TOTP时间
     * @return 一次性的验证码
     * @throws InvalidKeyException 非法key抛出的异常
     */
    public String createTotp(Key key, Instant time) throws InvalidKeyException {
        val password = timeBasedOneTimePasswordGenerator.generateOneTimePassword(key,time);
        val format = "%0" + PASSWORD_LENGTH + "d";
        return String.format(format, password);
    }

    public Optional<String> createTotp(String strKey) {
        try {
            return Optional.of(createTotp(decodeKeyFromString(strKey), Instant.now()));
        } catch (InvalidKeyException e) {
            return Optional.empty();
        }
    }

    /**
     * 验证TOTP
     * @param key the key to be used to generate the password timestamp
     * @param code 要验证的TOTP
     * @return 密码是否一致的布尔值
     * @throws InvalidKeyException 非法key抛出的异常
     */
    public boolean verifyTotp(Key key, String code) throws InvalidKeyException {
        val now = Instant.now();
        return code.equals(createTotp(key,now));
    }

    private Key generateKey() {
        return keyGenerator.generateKey();
    }

    private String encodeKeyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public String encodeKeyToString() {
        return encodeKeyToString(generateKey());
    }

    public Key decodeKeyFromString(String strKey) {
        return new SecretKeySpec(Base64.getDecoder()
                .decode(strKey),timeBasedOneTimePasswordGenerator.getAlgorithm()
        );
    }
}
