package ua.zxc.cowbot.service.impl;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ua.zxc.cowbot.redis.entity.UserHashEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheResolver = "cacheResolver")
public class UserCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public UserCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @CachePut(key = "#idKey")
    public Object save(String cacheName, String idKey, UserHashEntity user) {
        return user;
    }

    @Cacheable(key = "#idKey")
    public UserHashEntity get(String cacheName, String idKey) {
        return (UserHashEntity) redisTemplate.opsForValue().get(idKey);
    }

    public void getAllData(String cacheName) {
        Set<String> keys = redisTemplate.keys(cacheName + "*");
        List<Object> collect =
                keys.stream().map(s -> redisTemplate.opsForValue().get(s)).collect(Collectors.toList());

        System.out.println(collect);
    }
}
