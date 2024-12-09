package com.radical.be_radicalcare.ViewModels;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserPutVm(
        @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
        String fullName,

        @Email(message = "Invalid email format")
        String email,

        @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
        String phone,

        @Past(message = "Date of birth must be a past date")
        LocalDate doB,

        @Size(max = 255, message = "Address must not exceed 255 characters")
        String address
) {}
