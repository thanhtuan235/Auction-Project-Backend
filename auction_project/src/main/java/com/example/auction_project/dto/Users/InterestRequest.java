package com.example.auction_project.dto.Users;
import java.util.List;

public record InterestRequest(
    List<Integer> categoryIds
) {}
