package com.example.auction_project.service;

import org.springframework.data.redis.core.RedisTemplate;
import com.example.auction_project.dto.RedisMessage;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(RedisMessage message){
        redisTemplate.convertAndSend("auction_events", message);
    }
}
