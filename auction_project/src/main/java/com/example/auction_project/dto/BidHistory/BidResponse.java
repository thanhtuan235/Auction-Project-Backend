package com.example.auction_project.dto.BidHistory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BidResponse(
    Long id,
    UUID auctionId,
    String bidderName,
    BigDecimal amount,
    OffsetDateTime createdAt
) {}
