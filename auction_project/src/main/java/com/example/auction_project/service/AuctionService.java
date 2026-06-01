package com.example.auction_project.service;

import org.springframework.stereotype.Service;
import java.util.List;
import jakarta.transaction.Transactional;
import java.util.UUID;
import com.example.auction_project.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;

import com.example.auction_project.repository.AuctionRepository;
import com.example.auction_project.repository.CategoryRepository;
import com.example.auction_project.repository.UserRepository;
import com.example.auction_project.dto.Auction.AuctionRequest;
import com.example.auction_project.dto.Auction.AuctionResponse;

import com.example.auction_project.entity.Auction;
import com.example.auction_project.entity.Category;
import com.example.auction_project.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private AuctionResponse convertToResponse(Auction auction) {
        return new AuctionResponse(
            auction.getId(),
            auction.getSeller().getUsername(), 
            auction.getCategory().getId(), 
            auction.getTitle(),
            auction.getDescription(),
            auction.getImageUrls(),
            auction.getStartPrice(),
            auction.getBidStep(),
            auction.getCurrentPrice(),
            auction.getStatus(),
            auction.getStartAt(),
            auction.getEndAt()
        );
    }

    public List<AuctionResponse> findAll() {
        return auctionRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    public AuctionResponse findById(UUID id) {
        return auctionRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found"));
    }

    @Transactional
    public AuctionResponse create(AuctionRequest request, User seller) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.categoryId()));

        Auction auction = Auction.builder()
                .seller(seller)
                .category(category)
                .title(request.title())
                .description(request.description())
                .imageUrls(request.imageUrls())
                .startPrice(request.startPrice())
                .bidStep(request.bidStep())
                .currentPrice(request.startPrice())
                .status("PENDING")
                .startAt(request.startAt())
                .endAt(request.endAt())
                .build();

        Auction saved = auctionRepository.save(auction);        
        
        List<User> interestedUsers = userRepository.findByInterests_Id(category.getId())
                .stream()
                .filter(u -> !u.getId().equals(seller.getId()))
                .toList();

        if (!interestedUsers.isEmpty()) {
            notificationService.sendBulkNotificationAsync(
                    interestedUsers,
                    "NEW_ITEM",
                    "A new product you might be interested in has just been listed: " + saved.getTitle()
            );
        }
        return convertToResponse(auctionRepository.save(auction));
    }

    @Transactional
    public AuctionResponse update(UUID id, AuctionRequest request, String currentUsername) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + id));

        if (!auction.getSeller().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You don't have permission to edit this auction");
        }

        if (!"PENDING".equals(auction.getStatus())) {
            throw new IllegalStateException("Cannot edit auction that has already started or ended");
        }

        auction.setTitle(request.title());
        auction.setDescription(request.description());
        auction.setStartPrice(request.startPrice());
        auction.setBidStep(request.bidStep());
        auction.setStartAt(request.startAt());
        auction.setEndAt(request.endAt());

        if (request.imageUrls() != null) {
            auction.getImageUrls().clear();
            auction.getImageUrls().addAll(request.imageUrls());
        }

        if (!auction.getCategory().getId().equals(request.categoryId())) {
            Category newCategory = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            auction.setCategory(newCategory);
        }

        return convertToResponse(auctionRepository.save(auction));
    }

    @Transactional
    public void delete(UUID id, String currentUsername, boolean isAdmin) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found or already deleted"));

        if (!isAdmin && !auction.getSeller().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You don't have permission to delete this auction");
        }

        if (!"PENDING".equals(auction.getStatus())) {
            throw new IllegalStateException("Cannot delete an auction that is already in progress or closed");
        }

        auctionRepository.delete(auction);
    }
}