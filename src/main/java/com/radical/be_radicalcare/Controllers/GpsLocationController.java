package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.GpsLocation;
import com.radical.be_radicalcare.Services.GpsLocationService;
import com.radical.be_radicalcare.ViewModels.GpsLocationGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/gps")
@RequiredArgsConstructor
public class GpsLocationController {

    private final GpsLocationService gpsLocationService;

    // Lưu vị trí GPS
    @PostMapping
    public ResponseEntity<?> saveGpsLocation(@RequestBody GpsLocation gpsLocation) {
        // Kiểm tra `user`
        if (gpsLocation.getUser() == null || gpsLocation.getUser().getId() == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", "User ID is required");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra `customer` (nếu được cung cấp)
        if (gpsLocation.getCustomer() != null && gpsLocation.getCustomer().getId() == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", "If provided, Customer ID cannot be null");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            gpsLocationService.saveGpsLocation(gpsLocation);

            Map<String, Object> response = new HashMap<>();
            response.put("status", 201);
            response.put("message", "GPS location saved successfully");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error saving GPS location");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Lấy vị trí GPS
    @GetMapping("/{userId}")
    public ResponseEntity<?> getGpsLocation(@PathVariable String userId) {
        try {
            GpsLocation gpsLocation = gpsLocationService.getGpsLocationByUserId(userId);

            if (gpsLocation == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", 404);
                response.put("message", "GPS location not found");
                return ResponseEntity.status(404).body(response);
            }

            GpsLocationGetVm gpsLocationGetVm = GpsLocationGetVm.from(gpsLocation);

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "GPS location retrieved successfully");
            response.put("data", gpsLocationGetVm);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "Error retrieving GPS location");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
