package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.Customer;
import com.radical.be_radicalcare.Entities.User;
import com.radical.be_radicalcare.Repositories.ICustomerRepository;
import com.radical.be_radicalcare.Repositories.IUserRepository;
import com.radical.be_radicalcare.ViewModels.CustomerPostVm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final ICustomerRepository customerRepository;
    private final IUserRepository userRepository;

    public Optional<Customer> getCustomerByUserId(String userId) {
        return customerRepository.findByUserId_Id(userId);
    }
    public void updateCustomer(Customer customer) {
        customerRepository.save(customer);  // Lưu thông tin khách hàng
    }

    public void saveOrUpdateCustomer(String userId, CustomerPostVm customerPostVm) {
        log.info("Saving customer with dateOfBirth: {}", customerPostVm.doB());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerRepository.findByUserId_Id(userId)
                .orElse(new Customer());

        customer.setUserId(user);
        customer.setFullName(customerPostVm.fullName());
        customer.setDoB(customerPostVm.doB());
        customer.setAddress(customerPostVm.address());
        customer.setPhoneNumber(customerPostVm.phoneNumber());

        customerRepository.save(customer);
    }
}
