package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.MotorService;
import com.radical.be_radicalcare.Entities.Vehicle;
import lombok.Builder;

import java.util.List;

@Builder
public record SearchGetVm(
        String chassisNumber,
        String vehicleName,
        String version,
        String segment,
        String color,
        List<String> imageUrls,
        Double cost,
        String serviceName,         // Tên dịch vụ (MotorService)
        String serviceDescription,  // Mô tả dịch vụ (MotorService)
        String category,            // Danh mục dịch vụ (MotorService)
        Double price                // Giá dịch vụ (MotorService)
) {
    // Ánh xạ từ Vehicle entity
    public static SearchGetVm fromVehicleEntity(Vehicle vehicle) {
        return SearchGetVm.builder()
                .chassisNumber(vehicle.getChassisNumber())
                .vehicleName(vehicle.getVehicleName())
                .version(vehicle.getVersion())
                .segment(vehicle.getSegment())
                .color(vehicle.getColor())
                .imageUrls(vehicle.getImageUrls())
                .cost(vehicle.getCostId() != null ? vehicle.getCostId().getBaseCost() : 0.0)
                .build();
    }

    // Ánh xạ từ MotorService entity
    public static SearchGetVm fromMotorServiceEntity(MotorService motorService) {
        return SearchGetVm.builder()
                .serviceName(motorService.getServiceName()) // Tên dịch vụ
                .serviceDescription(motorService.getServiceDescription()) // Mô tả dịch vụ
                .price(motorService.getCostId() != null ? motorService.getCostId().getBaseCost() : 0.0) // Giá dịch vụ từ CostTable
                .build();
    }
}
