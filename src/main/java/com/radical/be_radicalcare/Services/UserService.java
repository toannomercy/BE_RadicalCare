package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Constants.Provider;
import com.radical.be_radicalcare.Constants.RoleType;
import com.radical.be_radicalcare.Controllers.AuthController;
import com.radical.be_radicalcare.Dto.RegisterRequest;
import com.radical.be_radicalcare.Entities.Role;
import com.radical.be_radicalcare.Entities.User;
import com.radical.be_radicalcare.Repositories.IRoleRepository;
import com.radical.be_radicalcare.Repositories.IUserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class UserService implements UserDetailsService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final EmailService emailService;

    public void registerUser(RegisterRequest registerRequest) {
        var user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setProvider(Provider.LOCAL);
        user.setRoles(Set.of(roleRepository.findRoleById(RoleType.USER.value)));

        userRepository.save(user);
        log.info("User registered with username: {}", registerRequest.getUsername());
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    public void forgotPassWord(String email) {
        var user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        log.info("User found with email: {}", email);

        String token = UUID.randomUUID().toString();

        // Lưu token vào user
        user.setTokenResetPassword(token);

        java.util.Date now = new java.util.Date();
        java.sql.Date expiryDate = new java.sql.Date(now.getTime() + 30 * 60 * 1000);
        user.setTokenResetPasswordExpired(expiryDate);

        userRepository.save(user);

        try {
            // Gửi token qua email
            emailService.sendMail(email, "Reset Password", token);
            log.info("Password reset email sent to {}", email);
        } catch (MessagingException e) {
            log.error("Error while sending email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Error while sending password reset email", e);
        }
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByTokenResetPassword(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token!"));

        java.sql.Date now = new java.sql.Date(System.currentTimeMillis());

        if (user.getTokenResetPasswordExpired().before(now)) {
            throw new IllegalArgumentException("Token expired!");
        }

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        user.setTokenResetPassword(null);
        user.setTokenResetPasswordExpired(null);
        userRepository.save(user);
    }

    public boolean isTokenValid(String token) {
        var userOptional = userRepository.findByTokenResetPassword(token);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        return user.getTokenResetPasswordExpired() != null &&
                user.getTokenResetPasswordExpired().after(new Date()); // Token hợp lệ nếu chưa hết hạn
    }

}

