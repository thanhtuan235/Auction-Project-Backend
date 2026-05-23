package com.example.auction_project.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.auction_project.entity.Auction;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
@Repository
public interface AuctionRepository extends JpaRepository<Auction, UUID>{
    @Lock(LockModeType.PESSIMISTIC_WRITE) // Block other transaction interrupt
    @Query("SELECT a FROM Auction a WHERE a.id = :id")
    Optional<Auction> findByIdWithLock(@Param("id") UUID id);

    List<Auction> findByStatusAndStartAtBefore(String status, OffsetDateTime time);
    List<Auction> findByStatusAndEndAtBefore(String status, OffsetDateTime time);
    List<Auction> findByEndAtBeforeAndWinnerIsNotNull(OffsetDateTime time);
}
