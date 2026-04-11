package com.example.auction_project.dto.Auction;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record AuctionRequest(
    @NotBlank String title,
    String description,
    @NotNull BigDecimal startPrice,
    @NotNull BigDecimal bidStep,
    @NotNull Integer categoryId,
    List<String> imageUrls,
    @NotNull LocalDateTime startAt,
    @NotNull LocalDateTime endAt
) {}
