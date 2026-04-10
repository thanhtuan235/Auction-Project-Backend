package com.example.auction_project.dto.Auction;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public record AuctionRequest(
    @NotBlank String title,
    String description,
    @NotNull BigDecimal startPrice,
    @NotNull BigDecimal bidStep,
    @NotNull Integer categoryId,
    @NotNull LocalDateTime startAt,
    @NotNull LocalDateTime endAt
) {}
