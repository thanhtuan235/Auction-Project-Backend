package com.example.auction_project.dto.Auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionResponse(
    UUID id,
    String sellerUsername, 
    Integer categoryId,   
    String title,
    String description,
    BigDecimal startPrice,
    BigDecimal bidStep,
    BigDecimal currentPrice,
    String status,
    LocalDateTime startAt,
    LocalDateTime endAt
) {}
