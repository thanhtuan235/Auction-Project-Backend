package com.example.auction_project.service;

import org.springframework.stereotype.Service;

import com.example.auction_project.entity.Auction;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;

import org.springframework.data.redis.core.RedisTemplate;

@Service
@RequiredArgsConstructor
public class RedisCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PRICE_PREFIX = "auction:%s:price";
    private static final String WINNER_PREFIX = "auction:%s:winner";
    
    // ======================== WRITE ========================

    public void cacheAuctionState(Auction auction){
        long ttlseconds = Duration.between(OffsetDateTime.now(), auction.getEndAt()).toSeconds();

        if(ttlseconds <= 0) return;

        String priceKey = String.format(PRICE_PREFIX, auction.getId());
        String winnerKey = String.format(WINNER_PREFIX, auction.getId());

        redisTemplate.opsForValue().set(priceKey, auction.getCurrentPrice(), Duration.ofSeconds(ttlseconds));

        if(auction.getWinner() != null){
            redisTemplate.opsForValue().set(winnerKey, auction.getWinner().getUsername(), Duration.ofSeconds(ttlseconds));
        }
    }

    // ======================== READ ========================

    public BigDecimal getCachedPrice(Auction auction){
        String priceKey = String.format(PRICE_PREFIX, auction.getId());
        Object value = redisTemplate.opsForValue().get(priceKey);

        if(value == null) return null;

        if(value instanceof BigDecimal) return (BigDecimal) value;
        return new BigDecimal(value.toString());
    }

    public String getCachedWinner(Auction auction){
        String winnerKey = String.format(WINNER_PREFIX, auction.getId());
        Object value = redisTemplate.opsForValue().get(winnerKey);
        return value != null ? value.toString() : null;
    }

    // ======================== DELETE ========================

    public void evictAuctionCache(String auctionId){
        redisTemplate.delete(String.format(PRICE_PREFIX, auctionId));
        redisTemplate.delete(String.format(WINNER_PREFIX,auctionId));
    }
}
