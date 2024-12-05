package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Appointment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record AppointmentGetVm(
        Long id,
        String customerId,
        String status,
        LocalDate dateCreated,
        List<AppointmentDetailGetVm> details) {

    public static AppointmentGetVm fromEntity(Appointment appointment) {
        return AppointmentGetVm.builder()
                .id(appointment.getId())
                .customerId(appointment.getCustomer().getId())
                .status(appointment.getStatus())
                .dateCreated(appointment.getDateCreated())
                .details(appointment.getAppointmentDetails().stream()
                        .map(AppointmentDetailGetVm::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}

