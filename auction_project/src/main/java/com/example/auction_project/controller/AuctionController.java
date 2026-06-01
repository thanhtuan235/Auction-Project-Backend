package com.example.auction_project.controller;

import com.example.auction_project.dto.Auction.AuctionRequest;
import com.example.auction_project.dto.Auction.AuctionResponse;
import com.example.auction_project.entity.User;
import com.example.auction_project.service.AuctionService;
import jakarta.validation.Valid;
import com.example.auction_project.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @GetMapping
    public ResponseEntity<List<AuctionResponse>> getAllAuctions() {
        return ResponseEntity.ok(auctionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> getAuctionById(@PathVariable UUID id) {
        return ResponseEntity.ok(auctionService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<AuctionResponse> createAuction(
            @Valid @RequestBody AuctionRequest request,
            @AuthenticationPrincipal User user) { 
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(auctionService.create(request, user));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<AuctionResponse> updateAuction(
            @PathVariable UUID id,
            @Valid @RequestBody AuctionRequest request,
            @AuthenticationPrincipal User user) { 

        return ResponseEntity.ok(auctionService.update(id, request, user.getUsername()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Void> deleteAuction(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        
         boolean isAdmin = Role.ADMIN.name().equals(user.getRole());
        auctionService.delete(id, user.getUsername(), isAdmin);

        return ResponseEntity.noContent().build();
    }
}