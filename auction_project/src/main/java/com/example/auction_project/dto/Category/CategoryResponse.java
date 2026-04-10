package com.example.auction_project.dto.Category;

public record CategoryResponse(
    Integer id,
    String name,
    String slug,
    Integer interestedCount
) {}