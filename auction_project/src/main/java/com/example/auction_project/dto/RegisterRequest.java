package com.example.auction_project.dto;

public record RegisterRequest(
        String username,
        String email,
        String password,
        String role
) {}