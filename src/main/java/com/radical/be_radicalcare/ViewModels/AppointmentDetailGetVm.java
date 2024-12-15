package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.AppointmentDetail;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AppointmentDetailGetVm(
        Long id,
        String serviceName,
        Double serviceCost,
        String serviceDescription,
        LocalDate serviceDate) {

    public static AppointmentDetailGetVm fromEntity(AppointmentDetail detail) {
        return AppointmentDetailGetVm.builder()
                .id(detail.getId())
                .serviceName(detail.getMotorService().getServiceName())
                .serviceCost(detail.getMotorService().getCostId().getBaseCost())
                .serviceDescription(detail.getDescription())
                .serviceDate(detail.getServiceDate())
                .build();
    }
}
