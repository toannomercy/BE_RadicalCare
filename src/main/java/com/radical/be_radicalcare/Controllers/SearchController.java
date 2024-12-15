package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Services.JwtTokenProvider;
import com.radical.be_radicalcare.Services.SearchService;
import com.radical.be_radicalcare.Services.RecentSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    private final RecentSearchService recentSearchService; // Service for managing recent searches
    private final JwtTokenProvider jwtTokenProvider;

//    // Lưu tìm kiếm vào lịch sử
//    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
//    @PostMapping("/recent")
//    public ResponseEntity<?> saveSearch(@RequestHeader("Authorization") String authorizationHeader,
//                                        @RequestParam String searchText) {
//        // Trích xuất userId từ token JWT
//        String token = authorizationHeader.replace("Bearer ", "");
//        String userId = jwtTokenProvider.getUserIdFromJWT(token); // Đảm bảo logic này đúng
//
//        // Lưu lịch sử tìm kiếm
//        recentSearchService.saveSearch(userId, searchText);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("status", "success");
//        response.put("message", "Search saved successfully");
//        return ResponseEntity.ok(response);
//    }

    // Lấy danh sách lịch sử tìm kiếm
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentSearches(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);

        List<Object> recentSearches = recentSearchService.getRecentSearches(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", recentSearches.isEmpty() ? "No recent searches found" : "Recent searches retrieved successfully");
        response.put("data", recentSearches);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/clear/recent")
    public ResponseEntity<?> clearRecentSearches(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody(required = false) Map<String, String> requestBody) {
        String token = authorizationHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);

        if (requestBody != null && requestBody.containsKey("searchText")) {
            String searchText = requestBody.get("searchText");
            recentSearchService.deleteSpecificSearch(userId, searchText);
        } else {
            recentSearchService.deleteRecentSearches(userId);
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Recent searches deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Xóa toàn bộ lịch sử tìm kiếm
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/delete/recent")
    public ResponseEntity<?> deleteRecentSearches(@RequestHeader("Authorization") String authorizationHeader) {
        // Trích xuất userId từ token JWT
        String token = authorizationHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);

        // Xóa lịch sử tìm kiếm của user
        recentSearchService.deleteRecentSearches(userId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Recent searches deleted successfully");
        return ResponseEntity.ok(response);
    }

}
