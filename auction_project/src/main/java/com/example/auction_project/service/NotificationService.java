package com.example.auction_project.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.example.auction_project.dto.Notification.NotificationAlertRequest;
import com.example.auction_project.dto.Notification.NotificationResponse;
import com.example.auction_project.repository.NotificationRepository;
import com.example.auction_project.repository.UserRepository;
import com.example.auction_project.entity.Notification;
import com.example.auction_project.entity.User;
import com.example.auction_project.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private NotificationResponse mapToNotificationResponse(Notification notification){
        return new NotificationResponse(
            notification.getId(),
            notification.getType(),
            notification.getMessage(),
            notification.getIsRead(),
            notification.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> findAllByUser(User user){
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);

        return notifications.stream()
            .map(this::mapToNotificationResponse)
            .toList();
    }

    public void markAsRead(Long id, User user) {
        int updatedRows = notificationRepository.markAsReadOptimized(id, user);
        if (updatedRows == 0) {
            throw new ResourceNotFoundException("Notification not found or unauthorized");
        }
    }

    @Transactional
    public NotificationResponse createNotification(NotificationAlertRequest request){
        User user = userRepository.findById(request.targetUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = Notification.builder()
                            .user(user)
                            .type("ALERT")
                            .message(request.message())
                            .isRead(false)
                            .build();

        System.out.println("request = " + request);
        System.out.println("targetUserId = " + request.targetUserId());

        return mapToNotificationResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void sendSystemNotification(User user, String type, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }
}
