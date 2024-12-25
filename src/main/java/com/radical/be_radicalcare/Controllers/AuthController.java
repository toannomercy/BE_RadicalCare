package com.radical.be_radicalcare.Controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.radical.be_radicalcare.Dto.JwtResponse;
import com.radical.be_radicalcare.Dto.LoginRequest;
import com.radical.be_radicalcare.Dto.RegisterRequest;
import com.radical.be_radicalcare.Entities.User;
import com.radical.be_radicalcare.Repositories.IRoleRepository;
import com.radical.be_radicalcare.Repositories.IUserRepository;
import com.radical.be_radicalcare.Services.*;
import com.radical.be_radicalcare.ViewModels.UserGetVm;
import com.radical.be_radicalcare.ViewModels.UserPutVm;
import io.github.cdimascio.dotenv.Dotenv;
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
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import com.radical.be_radicalcare.Entities.Customer;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final IUserRepository userRepository;
    private final OAuth2UserService oAuth2UserService;
    private final CustomerService customerService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ClientRegistrationRepository clientRegistrationRepository;

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
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
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

    //    @PostMapping("/oauth/google")
//    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> requestBody) {
//        try {
//            String idToken = requestBody.get("idToken");
//            if (idToken == null || idToken.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(Map.of("success", false, "message", "ID Token is required."));
//            }
//
//            log.info("Received ID Token: {}", idToken);
//
//            // Tải Google Public Keys
//            NetHttpTransport transport = new NetHttpTransport();
//            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
//            HttpRequestFactory requestFactory = transport.createRequestFactory();
//            HttpRequest request = requestFactory.buildGetRequest(new GenericUrl("https://www.googleapis.com/oauth2/v3/certs"));
//            HttpResponse response = request.execute();
//            String publicKeys = response.parseAsString();
//            log.info("Google Public Keys: {}", publicKeys);
//            log.info("Client ID from .env: {}", dotenv.get("OAUTH2_GOOGLE_CLIENT_ID_WEB"));
//
//            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
//                    .setAudience(Collections.singletonList(dotenv.get("OAUTH2_GOOGLE_CLIENT_ID_WEB"))) // Khớp Client ID
//                    .setIssuer("https://accounts.google.com") // Khớp issuer
//                    .build();
//            log.info("GoogleIdTokenVerifier audience: {}", verifier.getAudience());
//            log.info("GoogleIdTokenVerifier issuer: {}", verifier.getIssuer());
//            // Xác minh ID Token
//            GoogleIdToken googleIdToken = verifier.verify(idToken);
//            if (googleIdToken == null) {
//                log.error("Invalid ID Token: Signature verification failed");
//                throw new IllegalArgumentException("Invalid ID Token");
//            }
//
//            // Lấy payload từ ID Token
//            GoogleIdToken.Payload payload = googleIdToken.getPayload();
//            log.info("Verified Google ID Token Payload: {}", payload);
//
//            String googleId = payload.getSubject();
//            String email = payload.getEmail();
//            String fullName = (String) payload.get("name");
//
//            // Kiểm tra hoặc tạo user
//            User user = userService.findByUsername(googleId)
//                    .orElseGet(() -> {
//                        log.info("Creating new user with Google ID: {}", googleId);
//                        User newUser = userService.saveGoogleUser(googleId, email, fullName);
//                        customerService.createCustomerForUser(newUser);
//                        return newUser;
//                    });
//
//            // Tạo JWT token
//            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null);
//            String jwt = jwtTokenProvider.generateToken(authentication, user.getId(), null);
//
//            return ResponseEntity.ok(Map.of(
//                    "success", true,
//                    "token", jwt
//            ));
//        } catch (Exception e) {
//            log.error("Error during Google login: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("success", false, "message", "Google login failed: " + e.getMessage()));
//        }
//    }
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
}
