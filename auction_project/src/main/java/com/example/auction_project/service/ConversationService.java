package com.example.auction_project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import com.example.auction_project.entity.Auction;
import com.example.auction_project.entity.Conversation;
import com.example.auction_project.entity.Message;
import com.example.auction_project.entity.User;
import com.example.auction_project.repository.ConversationRepository;
import com.example.auction_project.repository.MessageRepository;
import com.example.auction_project.dto.Conversation.ConversationResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public void startAuctionChat(Auction auction){
        if(auction.getWinner() == null) return;

        Conversation conversation = Conversation.builder()
                                        .auction(auction)
                                        .participantOne(auction.getSeller())
                                        .participantTwo(auction.getWinner())
                                        .build();
        
        conversation = conversationRepository.save(conversation);

        Message welcomeMessage = Message.builder()
                                    .conversation(conversation)
                                    .type("SYSTEM")
                                    .content("Congratulations! " + auction.getWinner().getUsername() + " has won the bid. Let's start discussing the product." + auction.getTitle())
                                    .build();

        messageRepository.save(welcomeMessage);
    }

    public List<ConversationResponse> getMyConversations(User user){
        return conversationRepository.findAllByUser(user).stream()
            .map(conversation -> {
                User partner = conversation.getPartner(user);
                Message lastMsg = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conversation.getId());
                
                return new ConversationResponse(
                    conversation.getId(),
                    conversation.getAuction().getId(),
                    conversation.getAuction().getTitle(),
                    conversation.getAuction().getImageUrls().isEmpty() ? null : conversation.getAuction().getImageUrls().get(0),
                    partner.getId(),
                    partner.getUsername(),
                    lastMsg != null ? lastMsg.getContent() : "No message",
                    lastMsg != null ? lastMsg.getCreatedAt() : conversation.getCreatedAt(),
                    0 
                );
            }).toList();
    }
}
