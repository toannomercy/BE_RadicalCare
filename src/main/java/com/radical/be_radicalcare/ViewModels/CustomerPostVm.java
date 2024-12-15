package com.radical.be_radicalcare.ViewModels;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.radical.be_radicalcare.Entities.CostTable;
import com.radical.be_radicalcare.Entities.Customer;
import com.radical.be_radicalcare.Entities.MotorService;
import com.radical.be_radicalcare.Entities.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Builder
public record CustomerPostVm(
        String fullName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate doB,
        String address,
        String phoneNumber
) {
    public Customer toCustomer(User user) {
        Customer customer = new Customer();
        customer.setFullName(this.fullName);
        customer.setDoB(this.doB);
        customer.setAddress(this.address);
        customer.setPhoneNumber(this.phoneNumber);
        customer.setUserId(user);
        return customer;
    }
}