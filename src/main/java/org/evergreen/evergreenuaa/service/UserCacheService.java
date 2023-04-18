package org.evergreen.evergreenuaa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.evergreen.evergreenuaa.constant.Constants;
import org.evergreen.evergreenuaa.entity.User;
import org.evergreen.evergreenuaa.utils.CryptoUtil;
import org.evergreen.evergreenuaa.utils.TotpUtil;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserCacheService {

    private final RedissonClient redissonClient;
    private final TotpUtil totpUtil;
    private final CryptoUtil cryptoUtil;


    /**
     * 缓存mfa到redis中
     * @param user
     * @return
     */
    public String cacheUser(User user) {
        //随机生成12位的随机数mfaId
        val mfaId = cryptoUtil.randomAlphanumeric(12);
        RMapCache<String, User> cache = redissonClient.getMapCache(Constants.CACHE_MFA);
        if (!cache.containsKey(mfaId)) {
            cache.put(mfaId, user, totpUtil.getTimeStepInSeconds(), TimeUnit.SECONDS);
        }

        return mfaId;
    }

    /**
     * 使用mfaId去redis中查询User对象
     * @param mfaId
     * @return
     */
    public Optional<User> retrieveUser(String mfaId) {
        RMapCache<String,User> cache = redissonClient.getMapCache(Constants.CACHE_MFA);
        if (cache.containsKey(mfaId)) {
            return Optional.of(cache.get(mfaId));
        }
        return Optional.empty();
    }

    public Optional<User> verifyTotp(String mfaId, String code) {
        RMapCache<String,User> cache = redissonClient.getMapCache(Constants.CACHE_MFA);
        if (!cache.containsKey(mfaId) || cache.get(mfaId) == null) {
            return Optional.empty();
        }

        val cacheUser = cache.get(mfaId);
        try {
            val isValid = totpUtil.verifyTotp(totpUtil.decodeKeyFromString(cacheUser.getMfaKey()),code);
            if (!isValid) {
                return Optional.empty();
            }
            cache.remove(mfaId);
            return Optional.of(cacheUser);
        } catch (InvalidKeyException e) {
            log.error("用户Key {} 无效，具体错误：{}", cacheUser.getMfaKey(), e.getMessage());
        }
        return Optional.empty();
    }

}
