package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Customer;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CustomerGetVm(
        String id,
        String fullName,
        LocalDate doB,
        String address,
        UserGetVm user) {

    public static CustomerGetVm fromEntity(Customer customer) {
        return CustomerGetVm.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .doB(customer.getDoB())
                .address(customer.getAddress())
                .user(UserGetVm.fromEntity(customer.getUserId()))
                .build();
    }
}
