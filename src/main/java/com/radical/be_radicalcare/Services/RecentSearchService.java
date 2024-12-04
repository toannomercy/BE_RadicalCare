package com.radical.be_radicalcare.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class RecentSearchService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX = "recent_search:";
    private static final int MAX_HISTORY_SIZE = 20; // Giới hạn số lượng lịch sử tìm kiếm

    // Lưu tìm kiếm
    public void saveSearch(String userId, String searchText) {
        // Kiểm tra userId hợp lệ
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid userId: userId must not be null or empty");
        }

        String key = CACHE_PREFIX + userId;

        // Kiểm tra xem từ khóa đã tồn tại hay chưa
        List<Object> existingSearches = redisTemplate.opsForList().range(key, 0, -1);
        if (existingSearches != null && existingSearches.contains(searchText)) {
            // Nếu tồn tại, xóa từ khóa cũ
            redisTemplate.opsForList().remove(key, 1, searchText);
        }

        // Thêm từ khóa mới vào đầu danh sách
        redisTemplate.opsForList().leftPush(key, searchText);

        // Giới hạn số lượng lịch sử tìm kiếm
        redisTemplate.opsForList().trim(key, 0, MAX_HISTORY_SIZE - 1);

        // Đặt TTL cho key
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    // Lấy danh sách tìm kiếm gần đây
    public List<Object> getRecentSearches(String userId) {
        String key = CACHE_PREFIX + userId;
        return redisTemplate.opsForList().range(key, 0, -1); // Lấy toàn bộ danh sách từ Redis
    }

    // Xóa lịch sử tìm kiếm
    public void deleteRecentSearches(String userId) {
        String key = CACHE_PREFIX + userId;
        redisTemplate.delete(key); // Xóa key khỏi Redis
    }

    // Xóa một tìm kiếm cụ thể
    public void deleteSpecificSearch(String userId, String searchText) {
        String key = CACHE_PREFIX + userId;
        redisTemplate.opsForList().remove(key, 1, searchText); // Xóa một mục khớp với searchText
    }
}
