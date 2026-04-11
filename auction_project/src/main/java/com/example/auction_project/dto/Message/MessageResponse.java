package com.example.auction_project.dto.Message;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponse(
    Long id,
    UUID senderId,   
    String type,     
    String content,
    Boolean isRead,
    LocalDateTime createdAt,
    Boolean isMine   
) {}
