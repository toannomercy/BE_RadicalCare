package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Category;
import com.radical.be_radicalcare.Entities.Customer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
public record CustomerGetVm(
        String id,
        String fullName,
        LocalDate doB,
        String address,
        String phoneNumber
) {
    public static CustomerGetVm fromEntity(Customer customer) {
        return CustomerGetVm.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .doB(customer.getDoB())
                .address(customer.getAddress())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }
}
