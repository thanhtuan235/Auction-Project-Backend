package com.example.auction_project.service;

import java.io.IOException;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.auction_project.dto.RedisMessage;
import org.springframework.data.redis.connection.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMessageSubscriber implements MessageListener {
    private final SimpMessagingTemplate messageTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern){
        try {
            RedisMessage redisMessage = objectMapper.readValue(message.getBody(), RedisMessage.class);

            messageTemplate.convertAndSend(redisMessage.getTopic(), redisMessage.getPayload());
        } catch (IOException e){
            log.error("Failed to deserialize packet from Redis", e);
        }
    }
}