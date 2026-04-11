package com.example.auction_project.dto.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
    Long id,
    String type,
    String message,
    Boolean isRead,
    LocalDateTime createdAt
) {}
