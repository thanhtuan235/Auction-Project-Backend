package com.example.auction_project.dto.Users;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
    @NotBlank String role
) {}


