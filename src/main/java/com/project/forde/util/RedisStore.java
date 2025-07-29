package com.project.forde.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

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

    public<T> Optional<T> getJson(String storeKey, Class<T> valueType) {
        String jsonValue = (String) redisTemplate.opsForValue().get(storeKey);

        if (jsonValue == null) {
            return Optional.empty();
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return Optional.of(objectMapper.readValue(jsonValue, valueType));
        } catch (JsonProcessingException e) {
            log.error("Failed to convert JSON to object: {}", e.getMessage());
            throw new CustomException(ErrorCode.ERROR_REDIS);
        }
    }

    public void setJson(String storeKey, Object value, long ttlInMinutes) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(value);

            redisTemplate.opsForValue().set(storeKey, jsonValue, Duration.ofMinutes(ttlInMinutes));
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON: {}", e.getMessage());
            throw new CustomException(ErrorCode.ERROR_REDIS);
        }
    }

    public void deleteField(String storeKey, String hashKey) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(storeKey, hashKey);
    }

    public void deleteStore(String storeKey) {
        redisTemplate.delete(storeKey);
    }
}
