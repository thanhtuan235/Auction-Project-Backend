package com.example.auction_project.dto.Auction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;

public record AuctionResponse(
    UUID id,
    String sellerUsername, 
    Integer categoryId,   
    String title,
    String description,
    List<String> imageUrls,
    BigDecimal startPrice,
    BigDecimal bidStep,
    BigDecimal currentPrice,
    String status,
    OffsetDateTime startAt,
    OffsetDateTime endAt
) {}
