package com.example.auction_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.auction_project.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{

}
