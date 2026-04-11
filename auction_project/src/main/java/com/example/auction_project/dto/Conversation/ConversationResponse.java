package com.example.auction_project.dto.Conversation;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConversationResponse(
    Long id,
    UUID auctionId,
    String auctionTitle,
    String auctionImage,
    UUID partnerId,
    String partnerName,
    String lastMessage,
    LocalDateTime lastMessageAt,
    int unreadCount
) {}
