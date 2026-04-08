package com.example.auction_project.dto;

public record AuthRequest(
        String username,
        String password
) {}