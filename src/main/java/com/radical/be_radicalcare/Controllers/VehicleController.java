package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Services.*;
import com.radical.be_radicalcare.Specifications.VehicleSpecification;
import com.radical.be_radicalcare.ViewModels.SearchVehicleGetVm;
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
    private final JwtTokenProvider jwtTokenProvider;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/vehicles/search")
    public ResponseEntity<?> searchVehicles(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "chassisNumber") String sortBy,
            @RequestParam(required = false) List<String> segments,
            @RequestParam(required = false) List<String> colors,
            @RequestParam(required = false) Boolean sold,
            @RequestParam(required = false) List<Integer> categoryIds,
            @RequestParam(required = false) Double minCost,
            @RequestParam(required = false) Double maxCost
    ) {
        // Lấy userId từ JWT token
        String token = authorizationHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);

        try {
            // Tìm kiếm nhanh
            if (keyword != null && !keyword.trim().isEmpty() &&
                    segments == null && colors == null && sold == null &&
                    categoryIds == null && minCost == null && maxCost == null) {

                // Gọi SearchService để thực hiện tìm kiếm nhanh
                List<SearchVehicleGetVm> vehicles = searchService.searchVehiclesByKeyword(keyword);

                // Lưu lịch sử tìm kiếm
                recentSearchService.saveSearch(userId, keyword);

                // Chuẩn bị phản hồi
                Map<String, Object> response = new HashMap<>();
                response.put("status", 200);
                response.put("message", "Quick search successful");
                response.put("data", vehicles);
                return ResponseEntity.ok(response);
            }

            // Tìm kiếm chi tiết
            Specification<Vehicle> spec = Specification.where(
                            keyword == null || keyword.trim().isEmpty()
                                    ? null
                                    : VehicleSpecification.hasKeyword(keyword, categoryService)
                    )
                    .and(VehicleSpecification.hasSegmentIn(segments))
                    .and(VehicleSpecification.hasColorIn(colors))
                    .and(VehicleSpecification.isSold(sold))
                    .and(VehicleSpecification.hasCategoryIdIn(categoryIds))
                    .and(VehicleSpecification.hasCostBetween(minCost, maxCost));

            // Gọi SearchService để tìm kiếm chi tiết với phân trang
            Page<SearchVehicleGetVm> vehiclesPage = searchService.searchVehicles(spec, page, size, sortBy);

            // Chuẩn bị phản hồi
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Vehicles retrieved successfully");
            response.put("data", vehiclesPage.getContent());
            response.put("currentPage", vehiclesPage.getNumber());
            response.put("totalItems", vehiclesPage.getTotalElements());
            response.put("totalPages", vehiclesPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Xử lý lỗi
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error during search: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

//    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
//    @GetMapping("/vehicles")
//    public ResponseEntity<?> getAllVehicles(
//            @RequestParam(defaultValue = "0") int page, // Trang hiện tại (mặc định là 0)
//            @RequestParam(required = false) Integer size, // Kích thước trang, có thể null
//            @RequestParam(defaultValue = "chassisNumber") String sortBy
//    ) {
//        // Đặt kích thước mặc định nếu không được truyền
//        int pageSize = (size == null || size <= 0) ? Integer.MAX_VALUE : size;
//
//        // Gọi service để lấy dữ liệu phân trang
//        Page<Vehicle> vehiclePage = vehicleService.getAllVehicles(page, pageSize, sortBy);
//
//        // Chuyển đổi sang ViewModel
//        List<VehicleGetVm> vehicles = vehiclePage.getContent()
//                .stream()
//                .map(VehicleGetVm::fromEntity)
//                .toList();
//
//        // Chuẩn bị phản hồi
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", 200);
//        response.put("message", "Vehicles retrieved successfully");
//        response.put("data", vehicles);
//        response.put("currentPage", vehiclePage.getNumber());
//        response.put("totalItems", vehiclePage.getTotalElements());
//        response.put("totalPages", vehiclePage.getTotalPages());
//
//        return ResponseEntity.ok(response);
//    }
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/vehicles/all")
    public ResponseEntity<?> getAllVehiclesWithoutPagination(
            @RequestParam(defaultValue = "chassisNumber") String sortBy
    ) {
        // Lấy toàn bộ danh sách xe không phân trang
        List<Vehicle> allVehicles = vehicleService.getAllVehiclesWithoutPagination(sortBy);

        // Chuyển đổi sang ViewModel
        List<VehicleGetVm> vehicles = allVehicles
                .stream()
                .map(VehicleGetVm::fromEntity)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "All vehicles retrieved successfully");
        response.put("data", vehicles);

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
