package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.User;
import com.radical.be_radicalcare.Services.UserDeleteService;
import com.radical.be_radicalcare.Services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserDeleteService userDeleteService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        log.info("Request to delete user with ID: {}", userId);
        
        if (!userDeleteService.canDeleteUser(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User cannot be deleted or does not exist");
        }
        
        boolean deleted = userDeleteService.deleteUser(userId);
        
        if (deleted) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        Optional<User> user = userService.findById(userId);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 