package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Services.CategoryService;
import com.radical.be_radicalcare.Services.RecentSearchService;
import com.radical.be_radicalcare.Services.SearchService;
import com.radical.be_radicalcare.Services.VehicleService;
import com.radical.be_radicalcare.Specifications.VehicleSpecification;
import com.radical.be_radicalcare.ViewModels.SearchGetVm;
import com.radical.be_radicalcare.ViewModels.VehicleGetVm;
import com.radical.be_radicalcare.ViewModels.VehiclePostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class VehicleController {
    private final VehicleService vehicleService;
    private final CategoryService categoryService;
    private final SearchService searchService;
    private final RecentSearchService recentSearchService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/vehicles")
    public ResponseEntity<?> searchVehicles(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "chassisNumber") String sortBy,
            @RequestParam(required = false) List<String> segments,
            @RequestParam(required = false) List<String> colors,
            @RequestParam(required = false) Boolean sold,
            @RequestParam(required = false) List<Integer> categoryIds,
            @RequestParam(required = false) Double minCost,
            @RequestParam(required = false) Double maxCost,
            @RequestParam(required = false) String userId
    ) {
        // Xây dựng Specification
        Specification<Vehicle> spec = Specification.where(
                        keyword == null || keyword.trim().isEmpty()
                                ? null // Nếu keyword null, không áp dụng điều kiện keyword
                                : VehicleSpecification.hasKeyword(keyword, categoryService)
                )
                .and(VehicleSpecification.hasSegmentIn(segments))
                .and(VehicleSpecification.hasColorIn(colors))
                .and(VehicleSpecification.isSold(sold))
                .and(VehicleSpecification.hasCategoryIdIn(categoryIds))
                .and(VehicleSpecification.hasCostBetween(minCost, maxCost));

        // Thực hiện tìm kiếm
        Page<SearchGetVm> vehiclePage = searchService.searchVehicles(spec, page, size, sortBy);

        // Lưu lịch sử tìm kiếm
        String effectiveUserId = (userId == null || userId.isEmpty()) ? "anonymous" : userId;
        recentSearchService.saveSearch(effectiveUserId, keyword);

        // Chuẩn bị response
        List<SearchGetVm> vehicles = vehiclePage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Vehicles retrieved successfully");
        response.put("data", vehicles);
        response.put("currentPage", vehiclePage.getNumber());
        response.put("totalItems", vehiclePage.getTotalElements());
        response.put("totalPages", vehiclePage.getTotalPages());

        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/vehicle/{id}")
    public ResponseEntity<?> getVehicleById(@PathVariable String id) {
        return vehicleService.getVehicleById(id)
                .map(vehicle -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 200);
                    response.put("message", "Vehicle retrieved successfully");
                    response.put("data", VehicleGetVm.fromEntity(vehicle));

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 404);
                    response.put("message", "Vehicle not found");

                    return ResponseEntity.status(404).body(response);
                });
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/vehicle", consumes = "multipart/form-data")
    public ResponseEntity<?> createVehicle(@RequestPart("vehiclePostVm") VehiclePostVm vehiclePostVm,
                                           @RequestPart("images") List<MultipartFile> images) {
        try {
            vehicleService.addVehicle(vehiclePostVm.toEntity(), images);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 201);
            response.put("message", "Vehicle created successfully");

            return ResponseEntity.status(201).body(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Internal server error");

            return ResponseEntity.status(500).body(response);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/vehicle/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateVehicle(@PathVariable String id,
                                           @RequestPart("vehiclePostVm") VehiclePostVm vehiclePostVm,
                                           @RequestPart("images") List<MultipartFile> images) {
        try {
            vehicleService.updateVehicle(id, vehiclePostVm, images);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Vehicle updated successfully");

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Internal server error");

            return ResponseEntity.status(500).body(response);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/vehicle/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Vehicle deleted successfully");

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/vehicles/by-ids")
    public ResponseEntity<?> getVehiclesByIds(@RequestBody List<String> ids) {
        List<VehicleGetVm> vehicles = vehicleService.getVehiclesByIds(ids)
                .stream()
                .map(VehicleGetVm::fromEntity)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Vehicles retrieved successfully");
        response.put("data", vehicles);

        return ResponseEntity.ok(response);
    }
}
