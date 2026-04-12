package com.example.auction_project.dto.BidHistory;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record BidRequest(
    @NotNull UUID auctionId,
    @NotNull @Positive BigDecimal amount
) {}
