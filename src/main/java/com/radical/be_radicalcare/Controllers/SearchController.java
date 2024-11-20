package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Services.SearchService;
import com.radical.be_radicalcare.Services.RecentSearchService;
import com.radical.be_radicalcare.ViewModels.SearchGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService; // Service for general search
    private final RecentSearchService recentSearchService; // Service for managing recent searches

    // Search Vehicles by keyword and auto-save search history
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/vehicles")
    public ResponseEntity<?> searchVehicles(@RequestParam String keyword, @RequestParam(required = false) String userId) {
        // Perform search in the service
        List<SearchGetVm> vehicleViewModels = searchService.searchVehiclesByKeyword(keyword);

        // Save the search keyword with "anonymous" as fallback userId
        String effectiveUserId = (userId == null || userId.isEmpty()) ? "anonymous" : userId;
        recentSearchService.saveSearch(effectiveUserId, keyword);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Vehicles retrieved successfully");
        response.put("data", vehicleViewModels);
        return ResponseEntity.ok(response);
    }

    // Save search to RecentSearch cache
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/save")
    public ResponseEntity<?> saveSearch(@RequestParam String userId, @RequestParam String searchText) {
        recentSearchService.saveSearch(userId, searchText);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Search saved successfully");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/get")
    public ResponseEntity<?> getRecentSearches(@RequestParam(required = false) String userId) {
        String effectiveUserId = (userId == null || userId.isEmpty()) ? "anonymous" : userId;

        List<Object> recentSearches = recentSearchService.getRecentSearches(effectiveUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", recentSearches.isEmpty() ? "No recent searches found" : "Recent searches retrieved successfully");
        response.put("data", recentSearches);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRecentSearches(@RequestParam(defaultValue = "anonymous") String userId) {
        recentSearchService.deleteRecentSearches(userId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Recent searches deleted successfully");
        return ResponseEntity.ok(response);
    }
}
