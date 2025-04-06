package com.radical.be_radicalcare.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String senderId; // ID của người gửi

    @Column(nullable = false)
    private String recipientId; // ID của người nhận

    private String content; // Nội dung tin nhắn

    @Column(nullable = false)
    private String messageType; // Loại tin nhắn (TEXT, IMAGE, v.v.)

    private LocalDateTime timestamp; // Thời gian gửi tin nhắn

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageImage> images; // Danh sách các ảnh liên quan đến tin nhắn

    // Phương thức lấy danh sách URL của các ảnh
    public List<String> getImageUrls() {
        return images.stream()
                .map(MessageImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
