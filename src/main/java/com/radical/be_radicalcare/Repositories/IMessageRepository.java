package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IMessageRepository extends JpaRepository<Message, Long> {
    // Thêm phương thức để tìm lịch sử tin nhắn
    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :user1 AND m.recipientId = :user2) OR " +
            "(m.senderId = :user2 AND m.recipientId = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findChatHistoryBetween(String user1, String user2);

}
