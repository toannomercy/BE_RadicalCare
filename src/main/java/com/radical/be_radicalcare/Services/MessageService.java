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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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

    public List<ChatGetVm> getChatHistory(String user1, String user2) {
        // Gọi phương thức repository mới để lấy lịch sử chat hai chiều
        List<Message> chatHistory = messageRepository.findChatHistoryBetween(user1, user2);

        // Chuyển đổi từng Message thành ChatGetVm
        return chatHistory.stream()
                .map(message -> {
                    List<String> imageUrls = message.getImageUrls(); // Lấy danh sách URL từ MessageImage
                    return ChatGetVm.fromEntity(message, imageUrls);
                })
                .collect(Collectors.toList());
    }

    public void saveMessageImagesAsync(Message message, List<MultipartFile> images) {
        List<CompletableFuture<MessageImage>> futures = images.stream()
                .map(image -> CompletableFuture.supplyAsync(() -> {
                    try {
                        // Upload ảnh lên Cloudinary
                        Map<String, Object> uploadResult = cloudinaryService.upload(image);
                        String imageUrl = (String) uploadResult.get("url");

                        // Tạo MessageImage
                        MessageImage messageImage = new MessageImage();
                        messageImage.setImageUrl(imageUrl);
                        messageImage.setMessage(message);

                        return messageImage;
                    } catch (IOException e) {
                        // Ghi log lỗi
                        System.err.println("Failed to upload image: " + e.getMessage());
                        return null;
                    }
                }))
                .collect(Collectors.toList());

        // Thu thập kết quả và lưu tất cả vào DB
        List<MessageImage> messageImages = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull) // Loại bỏ các ảnh lỗi
                .collect(Collectors.toList());

        if (!messageImages.isEmpty()) {
            messageImageRepository.saveAll(messageImages);
        }
    }

}
