package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Appointment;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record AppointmentGetVm(
        Long id,
        LocalDate dateCreated,
        String status,
        CustomerGetVm customer,
        List<AppointmentDetailGetVm> appointmentDetails) {

    public static AppointmentGetVm fromEntity(Appointment appointment) {
        return AppointmentGetVm.builder()
                .id(appointment.getId())
                .dateCreated(appointment.getDateCreated())
                .status(appointment.getStatus())
                .customer(CustomerGetVm.fromEntity(appointment.getCustomer()))
                .appointmentDetails(appointment.getAppointmentDetails().stream()
                        .map(AppointmentDetailGetVm::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
