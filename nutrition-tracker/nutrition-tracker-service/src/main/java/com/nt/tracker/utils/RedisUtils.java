package com.nt.tracker.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisUtils {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public RedisUtils(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = new ObjectMapper(); // 可自定义配置
    }

    // ----------------- String -----------------

    // 保存对象，无过期时间
    public void set(String key, Object value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json);
            log.warn("已缓存key：" + key);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败", e);
        }
    }

    // 保存对象，带过期时间
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json, timeout, unit);
            log.warn("已缓存key：" + key + "，已设置过期时间：" + timeout + " " + unit);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败", e);
        }
    }

    // 获取对象
    public <T> T get(String key, Class<T> clazz) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) return null;
        try {
            log.warn("从缓存读取到key：" + key);
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("反序列化失败", e);
        }
    }

    // 适用于泛型（如 List<FoodVO>）
    public <T> T get(String key, TypeReference<T> typeReference) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) return null;
        try {
            log.warn("从缓存读取到key：" + key);
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis反序列化失败", e);
        }
    }

    // 获取 JSON 字符串
    public String getRawJson(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    // 删除 key
    public void delete(String key) {
        stringRedisTemplate.delete(key);
        log.warn("已删除key：" + key);
    }

    // 根据前缀批量删除 key
    public void deleteKeysByPrefix(String prefix) {
        Set<String> keys = stringRedisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
            log.warn("已批量删除keys：" + keys);
        }
    }


    // ----------------- Hash -----------------

    public void hSet(String key, String field, String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    public String hGet(String key, String field) {
        Object value = stringRedisTemplate.opsForHash().get(key, field);
        return value != null ? value.toString() : null;
    }

    public void hDel(String key, String... fields) {
        stringRedisTemplate.opsForHash().delete(key, (Object[]) fields);
    }

    public Boolean hHasKey(String key, String field) {
        return stringRedisTemplate.opsForHash().hasKey(key, field);
    }

    // ----------------- Set -----------------

    public Long sAdd(String key, String... values) {
        return stringRedisTemplate.opsForSet().add(key, values);
    }

    public Boolean sIsMember(String key, String value) {
        return stringRedisTemplate.opsForSet().isMember(key, value);
    }

    public Set<String> sMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    public Long sRemove(String key, String... values) {
        return stringRedisTemplate.opsForSet().remove(key, (Object[]) values);
    }

    // ----------------- Key -----------------

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }

    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }
}