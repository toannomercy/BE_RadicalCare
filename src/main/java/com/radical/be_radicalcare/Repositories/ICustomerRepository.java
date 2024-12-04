package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, String> {
}
