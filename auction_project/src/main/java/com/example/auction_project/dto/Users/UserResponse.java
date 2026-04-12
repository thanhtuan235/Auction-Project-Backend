package com.example.auction_project.dto.Users;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        String role,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}