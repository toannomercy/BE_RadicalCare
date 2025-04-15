package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Constants.RoleType;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.radical.be_radicalcare.Dto.JwtResponse;
import com.radical.be_radicalcare.Dto.LoginRequest;
import com.radical.be_radicalcare.Dto.RegisterRequest;

import com.radical.be_radicalcare.Entities.User;
import com.radical.be_radicalcare.Services.CustomerService;

import com.radical.be_radicalcare.Entities.Role;

import com.radical.be_radicalcare.Services.JwtTokenProvider;
import com.radical.be_radicalcare.Services.UserService;
import com.radical.be_radicalcare.Repositories.IUserRepository;
import com.radical.be_radicalcare.Services.*;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;
import com.radical.be_radicalcare.Entities.Customer;
import java.util.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final IUserRepository userRepository;
    private final CustomerService customerService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @PostMapping("/login")
    public ResponseEntity<?> loginMobile(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            User user = userService.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String userId = user.getId();
            String customerId = customerService.getCustomerByUserId(userId)
                    .map(Customer::getId)
                    .orElse(null);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication, userId, customerId);
            userService.updateOnlineStatus(userId, true);

            String role = user.getRoles().iterator().next().getName().name();
            return ResponseEntity.ok(new JwtResponse(jwt, user.getFullName(), role));
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        } catch (Exception e) {
            log.error("Error during authentication: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/login-web")
    public ResponseEntity<?> loginWeb(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response, HttpSession session) {
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
            jwtCookie.setSecure(false);
            jwtCookie.setMaxAge(24 * 60 * 60); // 1 ngày
            response.addCookie(jwtCookie);

            // Lưu vào session
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", role);

            return ResponseEntity.ok(new JwtResponse(jwt, user.getFullName(), role));
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            log.error("Error during web login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/logout-web")
    public ResponseEntity<?> logoutWeb(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            Cookie jwtCookie = new Cookie("token", null);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0);
            response.addCookie(jwtCookie);

            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            log.error("Error during web logout: {}", e.getMessage());
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

    @GetMapping("/online")
    public ResponseEntity<List<User>> getOnlineUsers() {
        List<User> onlineUsers = userService.getOnlineUsers();
        return ResponseEntity.ok(onlineUsers);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        String userId = ((UserDetails) authentication.getPrincipal()).getUsername();

        // Cập nhật trạng thái offline
        userService.updateOnlineStatus(userId, false);

        return ResponseEntity.ok("User logged out successfully");
    }

    @PostMapping("/oauth/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> requestBody) {
        try {
            String idTokenString = requestBody.get("idToken");

            if (idTokenString == null || idTokenString.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "ID Token is required."));
            }

            log.info("Received ID Token: {}", idTokenString);

            // Xác minh ID Token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(
                            clientRegistrationRepository.findByRegistrationId("google").getClientId()))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Invalid ID Token"));
            }

            // Lấy thông tin từ payload của ID Token
            GoogleIdToken.Payload payload = idToken.getPayload();
            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String fullName = (String) payload.get("name");

            log.info("Verified Google User - UUID: {}, Email: {}, Name: {}", googleId, email, fullName);

            // Kiểm tra hoặc tạo User và Customer trong database
            User user = userRepository.findByUsername(googleId);
            if (user == null) {
                log.info("Creating new User for Google ID: {}", googleId);
                user = userService.saveGoogleUser(googleId, email, fullName);
                customerService.createCustomerForUser(user);
            } else {
                // Check if customer exists, if not create one
                if (!customerService.getCustomerByUserId(user.getId()).isPresent()) {
                    customerService.createCustomerForUser(user);
                    log.info("Created missing Customer record for existing Google user: {}", googleId);
                }
            }

            // Tạo JWT Token
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), null, user.getAuthorities());
            String jwt = jwtTokenProvider.generateToken(authentication, user.getId(),
                    customerService.getCustomerByUserId(user.getId()).map(Customer::getId).orElse(null));

            // Trả JWT và thông tin người dùng về FE
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "token", jwt,
                    "userId", user.getId(),
                    "customerId", customerService.getCustomerByUserId(user.getId()).map(Customer::getId).orElse(""),
                    "roles", user.getRoles().stream().map(role -> role.getName()).toList(),
                    "username", user.getFullName(),
                    "email", user.getEmail()
            ));
        } catch (Exception e) {
            log.error("Error during Google login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Google login failed: " + e.getMessage()));
        }
    }
    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<?> handleGoogleOAuth2(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get customer ID if it exists
        String customerId = customerService.getCustomerByUserId(user.getId())
                .map(Customer::getId)
                .orElse(null);
        
        if (customerId == null) {
            // If no customer record exists, create one
            customerService.createCustomerForUser(user);
            customerId = customerService.getCustomerByUserId(user.getId())
                    .map(Customer::getId)
                    .orElse(null);
        }

        String jwt = jwtTokenProvider.generateToken(
                authentication,
                user.getId(),
                customerId
        );

        return ResponseEntity.ok(new JwtResponse(jwt, user.getFullName(), user.getRoles().iterator().next().getName().name()));
    }
}
