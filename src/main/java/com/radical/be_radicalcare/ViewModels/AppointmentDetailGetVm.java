package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.AppointmentDetail;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AppointmentDetailGetVm(
        Long id,
        String description,
        LocalDate serviceDate,
        Double serviceCost,
        MotorServiceGetVm motorService,
        Long appointmentId
) {
    public static AppointmentDetailGetVm fromEntity(AppointmentDetail appointmentDetail) {
        return AppointmentDetailGetVm.builder()
                .id(appointmentDetail.getId())
                .description(appointmentDetail.getDescription())
                .serviceDate(appointmentDetail.getServiceDate())
                .serviceCost(appointmentDetail.getServiceCost())
                .motorService(MotorServiceGetVm.from(appointmentDetail.getMotorService()))
                .appointmentId(appointmentDetail.getAppointment().getId())
                .build();
    }
}
