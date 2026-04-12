package com.example.auction_project.dto.Conversation;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ConversationResponse(
    Long id,
    UUID auctionId,
    String auctionTitle,
    String auctionImage,
    UUID partnerId,
    String partnerName,
    String lastMessage,
    OffsetDateTime lastMessageAt,
    int unreadCount
) {}
