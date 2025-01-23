package com.project.forde.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@AllArgsConstructor
public class RedisStore {
    private final RedisTemplate<String, Object> redisTemplate;

    public Object get(String storeKey, String hashKey) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(storeKey, hashKey);
    }

    public void set(String storeKey, String hashKey, Object value, long ttlInMinutes) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(storeKey, hashKey, value);

        redisTemplate.expire(storeKey, Duration.ofMinutes(ttlInMinutes));
    }

    public void deleteField(String storeKey, String hashKey) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(storeKey, hashKey);
    }

    public void deleteStore(String storeKey) {
        redisTemplate.delete(storeKey);
    }
}
