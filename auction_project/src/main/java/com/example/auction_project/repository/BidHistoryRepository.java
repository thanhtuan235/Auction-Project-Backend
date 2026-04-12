package com.example.auction_project.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.auction_project.entity.BidHistory;
import com.example.auction_project.entity.User;
import java.util.Optional;

@Repository
public interface BidHistoryRepository extends JpaRepository<BidHistory, Long>{
    List<BidHistory> findByAuctionIdOrderByAmountDesc(UUID auctionId);

    List<BidHistory> findByUserOrderByCreatedAt(User user);

    List<BidHistory> findTop2ByAuctionIdOrderByAmountDesc(UUID auctionId);
}
