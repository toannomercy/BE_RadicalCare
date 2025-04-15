package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.Customer;
import com.radical.be_radicalcare.Entities.User;
import com.radical.be_radicalcare.Repositories.ICustomerRepository;
import com.radical.be_radicalcare.Repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for safely deleting users and their associated data
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDeleteService {

    private final IUserRepository userRepository;
    private final ICustomerRepository customerRepository;

    /**
     * Deletes a user and all associated data with proper cascade
     * 
     * @param userId the ID of the user to delete
     * @return true if user was found and deleted, false otherwise
     */
    @Transactional
    public boolean deleteUser(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isEmpty()) {
            log.warn("Attempted to delete non-existent user with ID: {}", userId);
            return false;
        }
        
        User user = userOptional.get();
        log.info("Deleting user: {} (ID: {})", user.getUsername(), userId);
        
        // The deletion of the user will cascade to:
        // 1. Delete entries in user_role junction table due to cascade=REMOVE on @ManyToMany
        // 2. Delete the associated Customer due to CascadeType.ALL on customer field
        // 3. Delete associated Appointments, Contracts, and Invoices due to 
        //    cascades configured on the Customer entity
        userRepository.delete(user);
        
        log.info("User successfully deleted: {}", userId);
        return true;
    }
    
    /**
     * Check if a user can be safely deleted
     * 
     * @param userId the ID of the user to check
     * @return true if the user can be deleted safely, false otherwise
     */
    public boolean canDeleteUser(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isEmpty()) {
            return false;
        }
        
        // Add any business logic here that would prevent deletion
        // For example, check if user has active contracts, etc.
        
        return true;
    }
} 