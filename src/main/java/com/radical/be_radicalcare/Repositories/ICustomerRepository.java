package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findByUserId_Id(String userId);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
}
