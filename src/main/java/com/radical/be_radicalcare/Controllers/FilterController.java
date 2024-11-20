package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Services.FilterService;
import com.radical.be_radicalcare.ViewModels.FilterGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/filter")
@RequiredArgsConstructor
public class FilterController {

    private final FilterService filterService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<?> filterVehicles(@RequestParam MultiValueMap<String, String> queryParams) {
        // Lấy danh sách segments, nếu không có thì trả về danh sách rỗng
        List<String> segments = queryParams.getOrDefault("segment", Collections.emptyList());
        // Lấy danh sách colors, nếu không có thì trả về danh sách rỗng
        List<String> colors = queryParams.getOrDefault("color", Collections.emptyList());
        // Lấy giá trị sold, nếu không có thì là null
        Boolean sold = queryParams.containsKey("sold")
                ? Boolean.valueOf(queryParams.getFirst("sold"))
                : null;
        // Lấy danh sách categoryIds, nếu không có thì trả về danh sách rỗng
        List<Integer> categoryIds = queryParams.containsKey("categoryId")
                ? queryParams.get("categoryId").stream()
                .map(Integer::valueOf)
                .collect(Collectors.toList())
                : Collections.emptyList();
        // Lấy giá trị minCost và maxCost nếu có, nếu không thì là null
        Double minCost = queryParams.containsKey("minCost")
                ? Double.valueOf(Objects.requireNonNull(queryParams.getFirst("minCost")))
                : null;
        Double maxCost = queryParams.containsKey("maxCost")
                ? Double.valueOf(Objects.requireNonNull(queryParams.getFirst("maxCost")))
                : null;

        // Debug: In ra các tham số đã lấy
        System.out.println("Segments: " + segments);
        System.out.println("Colors: " + colors);
        System.out.println("Sold: " + sold);
        System.out.println("Category IDs: " + categoryIds);
        System.out.println("Min Cost: " + minCost);
        System.out.println("Max Cost: " + maxCost);

        // Gọi service với các tham số đã lấy được
        List<FilterGetVm> filteredVehicles = filterService.filterVehicles(
                segments, colors, sold, categoryIds, minCost, maxCost);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Vehicles filtered successfully");
        response.put("data", filteredVehicles);
        return ResponseEntity.ok(response);
    }

    // API để lấy các segment duy nhất
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/segments")
    public ResponseEntity<?> getSegments() {
        Set<String> segments = filterService.getUniqueSegments();
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("data", segments);
        return ResponseEntity.ok(response);
    }

    // API để lấy các màu sắc duy nhất
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/colors")
    public ResponseEntity<?> getColors() {
        Set<String> colors = filterService.getUniqueColors();
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("data", colors);
        return ResponseEntity.ok(response);
    }
}
