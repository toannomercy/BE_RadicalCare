package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Customer;
import com.radical.be_radicalcare.Services.CustomerService;
import com.radical.be_radicalcare.Services.JwtTokenProvider;
import com.radical.be_radicalcare.ViewModels.CustomerPostVm;
import com.radical.be_radicalcare.ViewModels.CustomerGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final JwtTokenProvider jwtTokenProvider;

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/profile")
    public ResponseEntity<?> getCustomerProfile(@RequestHeader("Authorization") String authorizationHeader) {
        // Extract userId from JWT token
        String token = authorizationHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);

        // Retrieve customer by userId
        return customerService.getCustomerByUserId(userId)
                .map(customer -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 200);
                    response.put("message", "Customer profile retrieved successfully");
                    response.put("data", CustomerGetVm.fromEntity(customer));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 404);
                    response.put("message", "Customer profile not found. Please update your profile.");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/profile")
    public ResponseEntity<?> updateCustomerProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CustomerPostVm customerPostVm) {
        String token = authorizationHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);

        customerService.saveOrUpdateCustomer(userId, customerPostVm);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Customer profile updated successfully");
        return ResponseEntity.ok(response);
    }
}
