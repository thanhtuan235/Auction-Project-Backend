package com.example.auction_project.service;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.auction_project.dto.RedisMessage;
import com.example.auction_project.dto.Notification.NotificationAlertRequest;
import com.example.auction_project.dto.Notification.NotificationResponse;
import com.example.auction_project.repository.NotificationRepository;
import com.example.auction_project.repository.UserRepository;
import com.example.auction_project.entity.Notification;
import com.example.auction_project.entity.User;
import com.example.auction_project.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final RedisMessagePublisher redisMessagePublisher;
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

        NotificationResponse response = mapToNotificationResponse(notificationRepository.save(notification));

        publishToUser(user.getUsername().toString(), response);

        return response;
    }

    @Transactional
    public void sendSystemNotification(User user, String type, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .isRead(false)
                .build();
        NotificationResponse response = mapToNotificationResponse(notificationRepository.save(notification));

        publishToUser(user.getUsername().toString(), response);
    }

     @Async("notificationExecutor")
    @Transactional
    public void sendBulkNotificationAsync(List<User> users, String type, String message) {
        for (User user : users) {
            try {
                sendSystemNotification(user, type, message);
            } catch (Exception e) {
                log.error("Failed to send notification to user {}: {}", user.getUsername(), e.getMessage());
            }
        }
    }

    private void publishToUser(String username, NotificationResponse response) {
        RedisMessage redisMessage =  RedisMessage.builder()
                                        .type("NOTIFICATION")
                                        .topic("/user/" + username + "/queue/notifications")
                                        .payload(response)
                                        .build();

        redisMessagePublisher.publish(redisMessage);
    }
}
