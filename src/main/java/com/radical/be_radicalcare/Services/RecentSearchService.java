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

    // Lưu tìm kiếm
    public void saveSearch(String userId, String searchText) {
        String key = CACHE_PREFIX + userId;
        redisTemplate.opsForList().rightPush(key, searchText); // Thêm từ khóa tìm kiếm vào danh sách
        redisTemplate.expire(key, Duration.ofDays(7)); // Đặt thời gian tồn tại (TTL) là 7 ngày
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
}
