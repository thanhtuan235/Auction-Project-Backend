package com.example.auction_project.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import java.util.List;

import com.example.auction_project.dto.BidHistory.BidRequest;
import com.example.auction_project.dto.BidHistory.BidResponse;
import com.example.auction_project.repository.AuctionRepository;
import com.example.auction_project.repository.BidHistoryRepository;

import com.example.auction_project.entity.User;
import com.example.auction_project.entity.Auction;
import com.example.auction_project.entity.BidHistory;
import com.example.auction_project.exception.ResourceNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BidService {
    private final BidHistoryRepository bidHistoryRepository;
    private final AuctionRepository auctionRepository;
    private final NotificationService notificationService;

    @Transactional
    public BidResponse placeBid(BidRequest request, User user){
        //Find auction and lock
        Auction auction = auctionRepository.findByIdWithLock(request.auctionId())
                .orElseThrow(() -> new ResourceNotFoundException("The auction does not exist."));
        
        User previousWinner = auction.getWinner();

        //Logic check own seller place bid
        if(auction.getSeller().getId().equals(user.getId())){
            throw new IllegalArgumentException("Sellers are not allowed to participate in auctions for their own products.");
        }

        //Logic check time
        OffsetDateTime now = OffsetDateTime.now();

        if(now.isBefore(auction.getStartAt()) || now.isAfter(auction.getEndAt())){
            throw new IllegalStateException("The auction has either not started or has already ended.");
        }

        //Logic check status
        if(!"OPEN".equals(auction.getStatus())){
            throw new IllegalStateException("The auction is not in operation.");
        }

        //Logic check bid amount
        BigDecimal minNextBid = auction.getCurrentPrice().add(auction.getBidStep());

        if(request.amount().compareTo(minNextBid) < 0){
            throw new IllegalArgumentException("The bid amount must be at least " + minNextBid);
        }

        auction.setCurrentPrice(request.amount());
        auction.setWinner(user);
        auctionRepository.save(auction);

        if (previousWinner != null && !previousWinner.getId().equals(user.getId())) {
            notificationService.sendSystemNotification(
                previousWinner, 
                "OUT_BID", 
                "You have been outbid on: " + auction.getTitle() + ". New price: " + auction.getCurrentPrice()
            );
        }

        BidHistory bidHistory = BidHistory.builder()
                                    .auction(auction)
                                    .user(user)
                                    .amount(request.amount())
                                    .build();
                    
        BidHistory saved = bidHistoryRepository.save(bidHistory);

        return new BidResponse(
            saved.getId(),
            auction.getId(),
            user.getUsername(),
            saved.getAmount(),
            saved.getCreatedAt()
        );
    }

    public List<BidResponse> getBidsByAuction(UUID auctionId){
        return bidHistoryRepository.findByAuctionIdOrderByAmountDesc(auctionId)
                    .stream()
                    .map(b -> new BidResponse(b.getId(), b.getAuction().getId(), b.getUser().getUsername(), b.getAmount(), b.getCreatedAt()))
                    .toList();
    }

}
