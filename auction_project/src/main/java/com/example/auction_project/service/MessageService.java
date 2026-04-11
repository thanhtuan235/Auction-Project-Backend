package com.example.auction_project.service;

import org.springframework.stereotype.Service;

import com.example.auction_project.dto.Message.MessageRequest;
import com.example.auction_project.dto.Message.MessageResponse;
import com.example.auction_project.entity.Conversation;
import com.example.auction_project.entity.User;
import com.example.auction_project.exception.ResourceNotFoundException;
import com.example.auction_project.repository.ConversationRepository;
import com.example.auction_project.repository.MessageRepository;

import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.example.auction_project.entity.Message;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    public List<MessageResponse> getMessages(Long conversationId, User currentUser) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("The conversation does not exist."));

        if (!conversation.getParticipantOne().getId().equals(currentUser.getId()) && 
            !conversation.getParticipantTwo().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to view this message!");
        }

        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(m -> new MessageResponse(
                    m.getId(),
                    m.getSender() != null ? m.getSender().getId() : null, 
                    m.getType(),
                    m.getContent(),
                    m.getIsRead(),
                    m.getCreatedAt(),
                    m.getSender() != null && m.getSender().getId().equals(currentUser.getId())
                )).toList();
    }
    

    @Transactional
    public MessageResponse sendMessage(MessageRequest request, User sender) {
        Conversation conv = conversationRepository.findById(request.conversationId())
                .orElseThrow(() -> new ResourceNotFoundException("The conversation does not exist."));

        if (!conv.getParticipantOne().getId().equals(sender.getId()) && 
            !conv.getParticipantTwo().getId().equals(sender.getId())) {
            throw new AccessDeniedException("You do not have permission to view this message!");
        }

        Message msg = Message.builder()
                .conversation(conv)
                .sender(sender)
                .type("TEXT")
                .content(request.content())
                .isRead(false)
                .build();

        Message saved = messageRepository.save(msg);
        
        return new MessageResponse(
            saved.getId(),
            sender.getId(),
            saved.getType(),
            saved.getContent(),
            saved.getIsRead(),
            saved.getCreatedAt(),
            true 
        );
    }


    @Transactional
    public void markAsRead(Long conversationId, User currentUser) {
        
        boolean isParticipant = conversationRepository.existsByIdAndParticipants(conversationId, currentUser);
        if (!isParticipant) {
            throw new AccessDeniedException("You are not in this conversation!");
        }

        messageRepository.markAllMessagesAsRead(conversationId, currentUser);
    }
}
