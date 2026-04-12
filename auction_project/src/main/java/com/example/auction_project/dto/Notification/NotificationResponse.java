package com.example.auction_project.dto.Notification;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record NotificationResponse(
    Long id,
    String type,
    String message,
    Boolean isRead,
    OffsetDateTime createdAt
) {}
