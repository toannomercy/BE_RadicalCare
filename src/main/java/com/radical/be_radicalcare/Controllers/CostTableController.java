package com.radical.be_radicalcare.Controllers;


import com.radical.be_radicalcare.Entities.CostTable;
import com.radical.be_radicalcare.Entities.WarrantyInfo;
import com.radical.be_radicalcare.Services.CostTableService;
import com.radical.be_radicalcare.ViewModels.CostTableGetVm;
import com.radical.be_radicalcare.ViewModels.CostTablePostVm;
import com.radical.be_radicalcare.ViewModels.WarrantyInfoGetVm;
import com.radical.be_radicalcare.ViewModels.WarrantyInfoPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CostTableController {
    private final CostTableService costTableService;

    @GetMapping("/cost-table")
    public ResponseEntity<?> getAllCostTable(){
        List<CostTableGetVm> costTables = costTableService.getAllCostTable()
                .stream()
                .map(CostTableGetVm::from)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "CostTable retrieved successfully");
        response.put("data", costTables);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cost-table/{id}")
    public ResponseEntity<?> getCostTableById(@PathVariable Long id){
        return costTableService.getCostTableById(id)
                .map(costTable -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 200);
                    response.put("message", "WarrantyInfo retrieved successfully");
                    response.put("data", CostTableGetVm.from(costTable));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 404);
                    response.put("message","CostTable not found");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PostMapping("/cost-table")
    public ResponseEntity<?> createCostTable (@RequestBody CostTablePostVm costTablePostVm){
        costTableService.addCostTable(costTablePostVm.toCostTable());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 201);
        response.put("message","CostTable create successfully");

        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/cost-table/{id}")
    public ResponseEntity<?> updateCostTable (@PathVariable Long id, @RequestBody CostTablePostVm costTablePostVm) {
        CostTable costTable = costTablePostVm.toCostTable();
        costTable.setCostId(id);
        costTableService.updateCostTable(costTable);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "CostTable updated successfully");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cost-table/{id}")
    public ResponseEntity<?> deleteCostTableById(@PathVariable Long id) {
        costTableService.deleteCostTable(id);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "CostTable deleted successfully");

        return ResponseEntity.ok(response);
    }
}
