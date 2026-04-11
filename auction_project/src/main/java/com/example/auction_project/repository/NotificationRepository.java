package com.example.auction_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.auction_project.entity.Notification;
import com.example.auction_project.entity.User;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{
    Optional<Notification> findByIdAndUser(Long id, User user);
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true " +
           "WHERE n.id = :id AND n.user = :user AND n.isRead = false")
    int markAsReadOptimized(@Param("id") Long id, @Param("user") User user);
}
