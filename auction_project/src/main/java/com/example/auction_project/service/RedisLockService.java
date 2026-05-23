package com.example.auction_project.service;


import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class RedisLockService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_PREFIX = "lock:auction:";
    private static final long LOCK_TTL = 5000;

    public String acquireLock(String auctionId){
        String lockKey = LOCK_PREFIX + auctionId;
        String lockValue = UUID.randomUUID().toString();

        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, LOCK_TTL, TimeUnit.MILLISECONDS);

        return Boolean.TRUE.equals(acquired) ? lockValue : null;
    }

    public void releaseLock(String auctionId, String lockValue){
        String lockKey = LOCK_PREFIX + auctionId;
        String currentValue = (String) redisTemplate.opsForValue().get(lockKey);

        if(lockValue.equals(currentValue)){
            redisTemplate.delete(lockKey);
        }
    }
}
