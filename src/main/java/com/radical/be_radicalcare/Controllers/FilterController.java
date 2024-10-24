package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Services.FilterService;
import com.radical.be_radicalcare.ViewModels.FilterGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/filter")
@RequiredArgsConstructor
public class FilterController {

    private final FilterService filterService;

    @GetMapping
    public ResponseEntity<?> filterVehicles(
            @RequestParam(required = false) String segment,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Boolean sold,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Double minCost,
            @RequestParam(required = false) Double maxCost
    ) {
        // Sử dụng ViewModel để trả về dữ liệu đã được định dạng
        List<FilterGetVm> filteredVehicles = filterService.filterVehicles(segment, color, sold, categoryId, minCost, maxCost)
                .stream()
                .map(FilterGetVm::from)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Vehicles filtered successfully");
        response.put("data", filteredVehicles);

        return ResponseEntity.ok(response);
    }

    // API để lấy các segment duy nhất
    @GetMapping("/segments")
    public ResponseEntity<?> getSegments() {
        Set<String> segments = filterService.getUniqueSegments();
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("data", segments);
        return ResponseEntity.ok(response);
    }

    // API để lấy các màu sắc duy nhất
    @GetMapping("/colors")
    public ResponseEntity<?> getColors() {
        Set<String> colors = filterService.getUniqueColors();
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("data", colors);
        return ResponseEntity.ok(response);
    }
}
