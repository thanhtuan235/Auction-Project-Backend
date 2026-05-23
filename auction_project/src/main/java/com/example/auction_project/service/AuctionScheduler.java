package com.example.auction_project.service;

import java.time.OffsetDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.auction_project.repository.AuctionRepository;

import java.util.List;
import com.example.auction_project.entity.Auction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionScheduler {
    private final AuctionRepository auctionRepository;
    private final ConversationService conversationService;
    private final NotificationService notificationService;

    //Scan per 1 minute
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processAuctions(){
        OffsetDateTime now = OffsetDateTime.now();

        //AUTO OPEN AUCTION: PENDING -> OPEN
        List<Auction> toStart = auctionRepository.findByStatusAndStartAtBefore("PENDING", now);
        for(Auction auction : toStart){
            auction.setStatus("OPEN");
            auctionRepository.save(auction);

            // LOGGING
            log.info("Auction '{}' Start!", auction.getTitle());
        }

        //AUTO CLOSE AUCTION: OPEN -> CLOSED
        List<Auction> toEnd = auctionRepository.findByStatusAndEndAtBefore("OPEN", now);
        for(Auction auction : toEnd){
            if(auction.getWinner() != null){
                auction.setStatus("CLOSED");

                conversationService.startAuctionChat(auction);

                notificationService.sendSystemNotification(
                    auction.getWinner(),
                    "WIN",
                    "Congratulations! You have won the auction: " + auction.getTitle()
                );

                //LOGGING
                log.info("Auction '{}' end. Winner: {}", auction.getTitle(), auction.getWinner().getUsername());
            } else {
                auction.setStatus("CLOSED");
                log.info("The session '{}' ended but no one joined.", auction.getTitle());
            }

            auctionRepository.save(auction);
        }
    }

    // !!! The features will be expanded later.
    //Winner Defaulting
    // @Scheduled(fixedRate = 3600000) 
    // @Transactional
    // public void handleDefaultingWinners() {
    //     OffsetDateTime deadline = OffsetDateTime.now().minusHours(24);

    //     List<Auction> defaultedAuctions = auctionRepository.findByEndAtBeforeAndWinnerIsNotNull(deadline);

    //     for (Auction auction : defaultedAuctions) {
    //         List<BidHistory> top2 = bidHistoryRepository.findTop2ByAuctionIdOrderByAmountDesc(auction.getId());
    //         Optional<BidHistory> secondBestBid =top2.size() >= 2
    //                                                 ? Optional.of(top2.get(1))
    //                                                 : Optional.empty();

    //         if (secondBestBid.isPresent()) {
    //             User newWinner = secondBestBid.get().getUser();
    //             auction.setWinner(newWinner);
    //             auction.setCurrentPrice(secondBestBid.get().getAmount());

    //             notificationService.sendSystemNotification(
    //                 newWinner, "WIN", 
    //                 "The previous winner has missed the payment deadline. You are the new winner for: " + auction.getTitle()
    //             );
    //         } else {
    //             auction.setStatus("CLOSED"); 
    //         }
    //         auctionRepository.save(auction);
    //     }
    // }
}
