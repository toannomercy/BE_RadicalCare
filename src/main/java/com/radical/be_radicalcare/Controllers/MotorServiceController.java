package com.radical.be_radicalcare.Controllers;


import com.radical.be_radicalcare.Entities.MotorService;
import com.radical.be_radicalcare.Services.MotorServicesService;
import com.radical.be_radicalcare.ViewModels.MotorServiceGetVm;
import com.radical.be_radicalcare.ViewModels.MotorServicePostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MotorServiceController {

    private final MotorServicesService motorServicesService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/motor-service")
    public ResponseEntity<?> getAllMotorServices(){
        List<MotorServiceGetVm> motorServices = motorServicesService.getAllMotorServices()
                .stream()
                .map(MotorServiceGetVm::from)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status",200);
        response.put("message","MotorServices retrieved successfully");
        response.put("data",motorServices);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/motor-service/{id}")
    public ResponseEntity<?> getMotorServiceById (@PathVariable Long id){
        return motorServicesService.getMotorServiceById(id)
                .map(motorService -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status",200);
                    response.put("message","MotorService retrieved successfully");
                    response.put("data", MotorServiceGetVm.from(motorService));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(()->{
                    Map<String, Object> response = new HashMap<>();
                    response.put("status",404);
                    response.put("message","MotorService not found");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/motor-service")
    public ResponseEntity<?> createMotorServiceById(@RequestBody MotorServicePostVm motorServicePostVm){
        motorServicesService.addMotorService(motorServicePostVm.toMotorService());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message","MotorService created successfully");

        return ResponseEntity.status(201).body(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/motor-service/{id}")
    public ResponseEntity<?> updateMotorServiceById (@PathVariable Long id, @RequestBody MotorServicePostVm motorServicePostVm){
        try{
            MotorService motorService = motorServicePostVm.toMotorService();
            motorService.setServiceId(id);
            motorServicesService.updateMotorService(motorService);

            Map<String ,Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message","MotorService update successfully");

            return ResponseEntity.status(201).body(response);
        }catch (Exception e){
            Map<String, Object> response = new HashMap<>();
            response.put("status",500);
            response.put("message","An error occurred while updating the MotorService");
            response.put("error",e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/motor-service/{id}")
    public ResponseEntity<?> deletedMotorServiceById(@PathVariable Long id){
        motorServicesService.deletedMotorServiceById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("status",200);
        response.put("message","MotorService deleted successfully");

        return ResponseEntity.status(200).body(response);
    }
}
