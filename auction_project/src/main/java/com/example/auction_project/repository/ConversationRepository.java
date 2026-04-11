package com.example.auction_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.auction_project.entity.Conversation;

import java.util.List;
import com.example.auction_project.entity.User;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long>{
    @Query("SELECT c FROM Conversation c WHERE c.participantOne = :user OR c.participantTwo = :user ORDER BY c.createdAt DESC")
    List<Conversation> findAllByUser(User user);

    @Query("SELECT COUNT(c) > 0 FROM Conversation c " +
           "WHERE c.id = :id AND (c.participantOne = :user OR c.participantTwo = :user)")
    boolean existsByIdAndParticipants(@Param("id") Long id, @Param("user") User user);
}
