package com.radical.be_radicalcare.ViewModels;

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
        Double cost
) {
    public static SearchGetVm fromEntity(Vehicle vehicle) {
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
}
