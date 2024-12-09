package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Dto.JwtResponse;
import com.radical.be_radicalcare.Dto.LoginRequest;
import com.radical.be_radicalcare.Dto.RegisterRequest;
import com.radical.be_radicalcare.Services.JwtTokenProvider;
import com.radical.be_radicalcare.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Xác thực người dùng
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Lấy userId từ cơ sở dữ liệu dựa trên username
            String userId = userService.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId(); // Lấy giá trị `id` từ bảng User

            // Tạo token JWT với Authentication và userId
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication, userId);

            // Trả về token trong response
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

        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password reset successfully");
    }
}


