package com.example.auction_project.dto.Notification;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record NotificationAlertRequest(
    @NotNull(message = "Users who need to receive notifications must be specified.")
    UUID targetUserId,

    @NotBlank(message = "The notification content must not be left blank.")
    String message
) {}
