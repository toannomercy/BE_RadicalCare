package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Constants.RoleType;
import com.radical.be_radicalcare.Dto.JwtResponse;
import com.radical.be_radicalcare.Dto.LoginRequest;
import com.radical.be_radicalcare.Dto.RegisterRequest;

import com.radical.be_radicalcare.Entities.User;
import com.radical.be_radicalcare.Services.CustomerService;

import com.radical.be_radicalcare.Entities.Role;

import com.radical.be_radicalcare.Services.JwtTokenProvider;
import com.radical.be_radicalcare.Services.UserService;
import com.radical.be_radicalcare.ViewModels.UserGetVm;
import com.radical.be_radicalcare.ViewModels.UserPutVm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.radical.be_radicalcare.Entities.Customer;

import java.util.Set;

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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response, HttpSession session) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            User user = userService.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String jwt = jwtTokenProvider.generateToken(authentication, user.getId(), null);
            String role = user.getRoles().iterator().next().getName().name();

            // Lưu JWT vào Cookie
            Cookie jwtCookie = new Cookie("token", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60); // 1 ngày
            response.addCookie(jwtCookie);

            // Lưu vào session
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", role);

            return ResponseEntity.ok(new JwtResponse(jwt, user.getFullName(), role));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // Xóa session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Xóa cookie JWT
        Cookie jwtCookie = new Cookie("token", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        return ResponseEntity.ok("Logout successful");
    }


    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<?> handleGoogleOAuth2(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = jwtTokenProvider.generateToken(
                authentication,
                user.getId(),
                null // Nếu không có Customer ID
        );

        return ResponseEntity.ok(new JwtResponse(jwt, user.getFullName(), user.getRoles().iterator().next().getName().name()));
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
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            userService.forgotPassWord(email);
            return ResponseEntity.ok("Password reset link sent to your email: " + email);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found: " + email);
        } catch (Exception e) {
            log.error("Error during forgot password: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing your request.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            boolean isTokenValid = userService.isTokenValid(token);
            if (!isTokenValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
            }
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (Exception e) {
            log.error("Error during password reset: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while resetting password.");
        }
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
            if (userPutVm.phone() != null) {
                customer.setPhoneNumber(userPutVm.phone());
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


