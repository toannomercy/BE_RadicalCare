package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Message;
import com.radical.be_radicalcare.Services.MessageService;
import com.radical.be_radicalcare.ViewModels.ChatGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;

    // API để gửi tin nhắn và/hoặc hình ảnh
    @PostMapping(value = "/send", consumes = {"multipart/form-data"})
    public ResponseEntity<?> sendMessage(
            @RequestPart(value = "message", required = false) Message message,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        // Kiểm tra nếu cả message và images đều null/empty
        if (message == null && (images == null || images.isEmpty())) {
            return ResponseEntity.noContent().build();
        }

        Message savedMessage = null;

        // Lưu nội dung tin nhắn nếu có
        if (message != null) {
            savedMessage = messageService.saveMessage(
                    message.getSenderId(),
                    message.getRecipientId(),
                    message.getContent(),
                    message.getMessageType()
            );
        }

        // Upload và lưu thông tin ảnh nếu có
        if (images != null && !images.isEmpty()) {
            if (savedMessage == null) {
                // Tạo tin nhắn mặc định nếu chỉ gửi hình ảnh
                savedMessage = messageService.saveMessage(
                        "system",
                        "unknown",
                        "Image sent",
                        "IMAGE"
                );
            }
            // Lưu ảnh và lấy URL
            messageService.saveMessageImages(savedMessage, images);
        }

        // Trả về phản hồi 200 OK
        return ResponseEntity.ok("Đã gửi thành công");
    }

    // API để lấy lịch sử chat giữa 2 người dùng
    @GetMapping("/history/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatGetVm>> getChatHistory(
            @PathVariable String senderId,
            @PathVariable String recipientId) {

        // Gọi service để lấy lịch sử đoạn chat
        List<ChatGetVm> chatHistory = messageService.getChatHistory(senderId, recipientId);

        return ResponseEntity.ok(chatHistory);
    }
}
