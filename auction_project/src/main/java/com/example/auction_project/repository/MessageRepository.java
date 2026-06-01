package com.example.auction_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.auction_project.entity.Message;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.example.auction_project.entity.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{
    Message findFirstByConversationIdOrderByCreatedAtDesc(Long conversationId);

    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    @Query("SELECT COUNT(m) FROM Message m " +
           "WHERE m.conversation.id = :convId " +
           "AND m.sender != :currentUser " +
           "AND m.isRead = false")
    int countUnreadMessages(@Param("convId") Long convId, @Param("currentUser") User currentUser);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true " +
           "WHERE m.conversation.id = :convId " +
           "AND m.sender != :currentUser " + 
           "AND m.isRead = false")
    int markAllMessagesAsRead(@Param("convId") Long convId, @Param("currentUser") User currentUser);
}
