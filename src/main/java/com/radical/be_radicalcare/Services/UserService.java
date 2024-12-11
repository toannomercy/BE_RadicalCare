package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Constants.Provider;
import com.radical.be_radicalcare.Constants.RoleType;
import com.radical.be_radicalcare.Dto.RegisterRequest;

import com.radical.be_radicalcare.Entities.Customer;
import com.radical.be_radicalcare.Entities.Role;

import com.radical.be_radicalcare.Entities.User;
import com.radical.be_radicalcare.Repositories.ICustomerRepository;
import com.radical.be_radicalcare.Repositories.IRoleRepository;
import com.radical.be_radicalcare.Repositories.IUserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final ICustomerRepository customerRepository;

    private final IRoleRepository roleRepository;
    private final EmailService emailService;



    public void registerUser(RegisterRequest registerRequest) {
        // Tạo đối tượng User và ánh xạ dữ liệu từ RegisterRequest
        var user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setFullName(registerRequest.getFullName());
        user.setPassword(new BCryptPasswordEncoder().encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setProvider(Provider.LOCAL);
        user.setFullName(registerRequest.getFullName()); // Ánh xạ fullname
        user.setPhone(registerRequest.getPhoneNumber()); // Ánh xạ phone
        user.setRoles(Set.of(roleRepository.findRoleById(RoleType.USER.value)));

        // Lưu user vào cơ sở dữ liệu
        userRepository.save(user);

        // Tạo đối tượng Customer và ánh xạ dữ liệu từ RegisterRequest
        var customer = new Customer();
        customer.setFullName(registerRequest.getFullName());
        customer.setPhoneNumber(registerRequest.getPhoneNumber());
        customer.setAddress(registerRequest.getAddress());
        customer.setDoB(registerRequest.getDoB());
        customer.setUserId(user);

        // Lưu customer vào cơ sở dữ liệu
        customerRepository.save(customer);
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

        log.info("User found with username: {}", username);

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

    public void updateUser(User user) {
        userRepository.save(user);  // Lưu thông tin người dùng
    }

    public void forgotPassWord(String email) {
        var user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        log.info("User found with email: {}", email);

        String token = UUID.randomUUID().toString();
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

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        user.setTokenResetPassword(null);
        user.setTokenResetPasswordExpired(null);
        userRepository.save(user);
    }

    public boolean isTokenValid(String token) {
        User user = userRepository.findByTokenResetPassword(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token!"));

        java.sql.Date now = new java.sql.Date(System.currentTimeMillis());

        return user.getTokenResetPasswordExpired().after(now);
    }
}

