package com.example.auction_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.auction_project.entity.Notification;
import com.example.auction_project.entity.User;

import java.util.Optional;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{
    Optional<Notification> findByIdAndUser(Long id, User user);
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}
