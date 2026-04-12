package com.example.auction_project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auction_project.service.ConversationService;
import com.example.auction_project.service.MessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import com.example.auction_project.dto.Conversation.ConversationResponse;
import com.example.auction_project.dto.Message.MessageRequest;
import com.example.auction_project.dto.Message.MessageResponse;
import com.example.auction_project.entity.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final MessageService messageService;
    private final ConversationService conversationService;
    
    @GetMapping("")
    public ResponseEntity<List<ConversationResponse>> getAllConversation(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(conversationService.getMyConversations(user));
    }
    

    //MESSAGE
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageResponse>> getMessage(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getMessages(id, user));
    }
    
    @PostMapping("/messages")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.sendMessage(request, user));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markConversationAsRead(@PathVariable Long id, @AuthenticationPrincipal User user) {
        messageService.markAsRead(id, user); 
        return ResponseEntity.noContent().build(); 
    }
    
}
