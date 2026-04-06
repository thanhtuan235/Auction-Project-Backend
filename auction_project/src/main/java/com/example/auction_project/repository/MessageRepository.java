package com.example.auction_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.auction_project.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{

}
