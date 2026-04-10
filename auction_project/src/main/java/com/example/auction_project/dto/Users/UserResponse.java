package com.example.auction_project.dto.Users;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        String role,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}