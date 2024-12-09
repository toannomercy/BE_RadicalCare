package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Dto.JwtResponse;
import com.radical.be_radicalcare.Dto.LoginRequest;
import com.radical.be_radicalcare.Dto.RegisterRequest;
import com.radical.be_radicalcare.Entities.User;
import com.radical.be_radicalcare.Services.CustomerService;
import com.radical.be_radicalcare.Services.JwtTokenProvider;
import com.radical.be_radicalcare.Services.UserService;
import com.radical.be_radicalcare.ViewModels.UserGetVm;
import com.radical.be_radicalcare.ViewModels.UserPutVm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.radical.be_radicalcare.Entities.Customer;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final CustomerService customerService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            String userId = userService.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();
            String customerId = customerService.getCustomerByUserId(userId)
                    .map(Customer::getId)
                    .orElse(null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication, userId, customerId);

            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or Password");
        } catch (Exception e) {
            log.error("Error during authentication: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/fetch-user")
    public ResponseEntity<UserGetVm> fetchUser(Authentication authentication) {
        try {
            String username = authentication.getName();

            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserGetVm userGetVm = UserGetVm.fromEntity(user);

            return ResponseEntity.ok(userGetVm);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username is already taken");
        }

        userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email){
        userService.forgotPassWord(email);
        return ResponseEntity.ok("Password reset link sent to your email: " + email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        boolean isTokenValid = userService.isTokenValid(token);
        if (!isTokenValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token không hợp lệ hoặc đã hết hạn.");
        }

        userService.resetPassword(token, newPassword);  // Reset mật khẩu dựa trên token
        return ResponseEntity.ok("Password reset successfully");
    }

    @PutMapping("/update-profile")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<?> updateUserAndCustomerProfile(Authentication authentication,
                                                          @RequestBody UserPutVm userPutVm) {
        try {
            String username = authentication.getName();

            // Lấy thông tin User
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Cập nhật thông tin User (chỉ cập nhật nếu không null)
            if (userPutVm.fullName() != null) {
                user.setFullName(userPutVm.fullName());
            }
            if (userPutVm.email() != null) {
                user.setEmail(userPutVm.email());
            }
            if (userPutVm.phone() != null) {
                user.setPhone(userPutVm.phone());
            }
            userService.updateUser(user);

            // Lấy thông tin Customer liên kết với User
            Customer customer = customerService.getCustomerByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            // Cập nhật thông tin Customer (chỉ cập nhật nếu không null)
            if (userPutVm.doB() != null) {
                customer.setDoB(userPutVm.doB());
            }
            if (userPutVm.address() != null) {
                customer.setAddress(userPutVm.address());
            }
            if (userPutVm.fullName() != null) {
                customer.setFullName(userPutVm.fullName());
            }
            customerService.updateCustomer(customer);

            return ResponseEntity.ok("User and Customer profile updated successfully");
        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

}


