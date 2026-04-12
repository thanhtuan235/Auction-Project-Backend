package com.example.auction_project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auction_project.dto.BidHistory.BidRequest;
import com.example.auction_project.dto.BidHistory.BidResponse;
import com.example.auction_project.service.BidService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.auction_project.entity.User;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;

    @PostMapping
    public ResponseEntity<BidResponse> placeBid(@Valid @RequestBody BidRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bidService.placeBid(request, user));
    }
 
    @GetMapping("/auctions/{id}")
    public ResponseEntity<List<BidResponse>> getAuctionHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(bidService.getBidsByAuction(id));
    }
    
}
