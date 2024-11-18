package com.radical.be_radicalcare.ViewModels;


import com.radical.be_radicalcare.Entities.MotorService;
import lombok.Builder;

@Builder
public record MotorServiceGetVm(
        Long serviceId,
        String serviceName,
        String serviceDescription,
        Boolean isDeleted,
        Long costTableId) {

    public static MotorServiceGetVm from (MotorService motorService) {
        return MotorServiceGetVm.builder()
                .serviceId(motorService.getServiceId())
                .serviceName(motorService.getServiceName())
                .serviceDescription(motorService.getServiceDescription())
                .isDeleted(motorService.getIsDeleted())
                .costTableId(motorService.getCostId() != null ? motorService.getCostId().getCostId(): null)
                .build();
    }
}
