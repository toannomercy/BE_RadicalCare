package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.WarrantyInfo;
import com.radical.be_radicalcare.Services.WarrantyInfoService;
import com.radical.be_radicalcare.ViewModels.WarrantyInfoGetVm;
import com.radical.be_radicalcare.ViewModels.WarrantyInfoPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
@RequiredArgsConstructor
public class WarrantyInfoController {
    private final WarrantyInfoService warrantyInfoService;

    @GetMapping("/warranty-info")
    public ResponseEntity<?> getAllWarrantyInfos() {
        List<WarrantyInfoGetVm> warrantyInfos = warrantyInfoService.getAllWarrantyInfos()
                .stream()
                .map(WarrantyInfoGetVm::from)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "WarrantyInfos retrieved successfully");
        response.put("data", warrantyInfos);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/warranty-info/{id}")
    public ResponseEntity<?> getWarrantyInfoById(@PathVariable Long id) {
        return warrantyInfoService.getWarrantyInfoById(id)
                .map(warrantyInfo -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 200);
                    response.put("message", "WarrantyInfo retrieved successfully");
                    response.put("data", WarrantyInfoGetVm.from(warrantyInfo));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 404);
                    response.put("message", "WarrantyInfo not found");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PostMapping("/warranty-info")
    public ResponseEntity<?> createWarrantyInfo(@RequestBody WarrantyInfoPostVm warrantyInfoPostVm) {
        warrantyInfoService.addWarrantyInfo(warrantyInfoPostVm.toWarrantyInfo());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 201);
        response.put("message", "WarrantyInfo created successfully");

        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/warranty-info/{id}")
    public ResponseEntity<?> updateWarrantyInfo(@PathVariable Long id, @RequestBody WarrantyInfoPostVm warrantyInfoPostVm) {
        WarrantyInfo warrantyInfo = warrantyInfoPostVm.toWarrantyInfo();
        warrantyInfo.setId(id);
        warrantyInfoService.updateWarrantyInfo(warrantyInfo);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "WarrantyInfo updated successfully");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/warranty-info/{id}")
    public ResponseEntity<?> deleteWarrantyInfoById(@PathVariable Long id) {
        warrantyInfoService.deleteWarrantyInfoById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "WarrantyInfo deleted successfully");

        return ResponseEntity.ok(response);
    }
}
