package com.example.auction_project.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.auction_project.entity.Auction;
import java.util.UUID;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, UUID>{

}
