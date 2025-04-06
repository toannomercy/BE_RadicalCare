package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.MessageImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IMessageImageRepository extends JpaRepository<MessageImage, Long> {
    List<MessageImage> findByMessageId(Long messageId);
}
