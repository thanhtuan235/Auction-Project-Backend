package com.example.auction_project.dto.Message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record MessageRequest(
    @NotNull Long conversationId,
    @NotBlank String content
) {}
