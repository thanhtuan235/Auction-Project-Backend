package com.example.auction_project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auction_project.service.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.auction_project.dto.Notification.NotificationAlertRequest;
import com.example.auction_project.dto.Notification.NotificationResponse;
import com.example.auction_project.entity.User;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotification(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.findAllByUser(user));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> updateIsReadNotification(@PathVariable Long id, @AuthenticationPrincipal User user){
        return ResponseEntity.ok(notificationService.updateIsRead(id, user));
    }
    
    @PostMapping("/alert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationResponse> createAlertNotification(@RequestBody NotificationAlertRequest request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }
    
}
