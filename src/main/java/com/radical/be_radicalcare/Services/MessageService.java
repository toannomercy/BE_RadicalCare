package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.Message;
import com.radical.be_radicalcare.Entities.MessageImage;
import com.radical.be_radicalcare.Repositories.IMessageRepository;
import com.radical.be_radicalcare.Repositories.IMessageImageRepository;
import com.radical.be_radicalcare.ViewModels.ChatGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class MessageService {

    private final IMessageRepository messageRepository;
    private final IMessageImageRepository messageImageRepository;
    private final CloudinaryService cloudinaryService;

    public Message saveMessage(String senderId, String recipientId, String content, String messageType) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public List<ChatGetVm> getChatHistory(String senderId, String recipientId) {
        List<Message> chatHistory = messageRepository.findBySenderIdAndRecipientIdOrderByTimestampAsc(senderId, recipientId);

        return chatHistory.stream()
                .map(message -> {
                    List<String> imageUrls = message.getImageUrls(); // Lấy danh sách URL từ MessageImage
                    return ChatGetVm.fromEntity(message, imageUrls);
                })
                .collect(Collectors.toList());
    }

    public void saveMessageImages(Message message, List<MultipartFile> images) {
        for (MultipartFile image : images) {
            try {
                // Upload ảnh lên Cloudinary
                Map<String, Object> uploadResult = cloudinaryService.upload(image);
                String imageUrl = (String) uploadResult.get("url");

                // Tạo MessageImage và lưu vào DB
                MessageImage messageImage = new MessageImage();
                messageImage.setImageUrl(imageUrl);
                messageImage.setMessage(message);

                messageImageRepository.save(messageImage);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }
    }
}
