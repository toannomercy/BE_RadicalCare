package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Message;
import com.radical.be_radicalcare.Services.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;
    // API để gửi tin nhắn và/hoặc hình ảnh
    @PostMapping(value = "/send", consumes = {"multipart/form-data"})
    public ResponseEntity<?> sendMessage(
            @RequestPart(value = "message", required = false) Message message,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        if (message != null) {
            if (message.getSenderId() == null || message.getRecipientId() == null) {
                return ResponseEntity.badRequest().body("SenderId and RecipientId are required.");
            }
        }

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
            messageService.saveMessageImagesAsync(savedMessage, images);
        }

        // Trả về phản hồi 200 OK
        return ResponseEntity.ok("Đã gửi thành công");
    }

    @GetMapping("/userinfo")
    public ResponseEntity<Map<String, String>> getUserOrStaffInfo(@RequestHeader("Authorization") String token) {
        // Bỏ tiền tố "Bearer "
        token = token.startsWith("Bearer ") ? token.substring(7) : token;

        try {
            String id = jwtTokenProvider.getUserIdOrStaffId(token);
            return ResponseEntity.ok(Map.of("id", id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
    // API để lấy lịch sử chat giữa 2 người dùng
    @GetMapping("/history/{user1}/{user2}")
    public ResponseEntity<List<ChatGetVm>> getChatHistory(
            @PathVariable String user1,
            @PathVariable String user2) {

        // Gọi service để lấy lịch sử đoạn chat hai chiều
        List<ChatGetVm> chatHistory = messageService.getChatHistory(user1, user2);

        if (chatHistory.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(chatHistory);
    }
}
