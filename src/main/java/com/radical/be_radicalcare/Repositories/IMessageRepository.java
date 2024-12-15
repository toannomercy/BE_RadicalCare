package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IMessageRepository extends JpaRepository<Message, Long> {
    // Thêm phương thức để tìm lịch sử tin nhắn
    List<Message> findBySenderIdAndRecipientIdOrderByTimestampAsc(String senderId, String recipientId);
}
