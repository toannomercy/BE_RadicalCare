package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Services.VehicleService;
import com.radical.be_radicalcare.ViewModels.VehicleGetVm;
import com.radical.be_radicalcare.ViewModels.VehiclePostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class VehicleController {
    private final VehicleService vehicleService;

    @GetMapping("/vehicle")
    public ResponseEntity<?> getAllVehicles() {
        List<VehicleGetVm> vehicles = vehicleService.getAllVehicles()
                .stream()
                .map(VehicleGetVm::fromEntity)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Vehicles retrieved successfully");
        response.put("data", vehicles);

        return ResponseEntity.ok(response);
    }

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

    @DeleteMapping("/vehicle/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Vehicle deleted successfully");

        return ResponseEntity.ok(response);
    }

}

