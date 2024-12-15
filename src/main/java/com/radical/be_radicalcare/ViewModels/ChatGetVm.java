package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Message;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ChatGetVm(
        Long id,
        String senderId,
        String recipientId,
        String content,
        String messageType,
        LocalDateTime timestamp,
        List<String> uploadedImages
) {
    public static ChatGetVm fromEntity(Message message, List<String> uploadedImages) {
        return ChatGetVm.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .recipientId(message.getRecipientId())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .timestamp(message.getTimestamp())
                .uploadedImages(uploadedImages)
                .build();
    }
}
