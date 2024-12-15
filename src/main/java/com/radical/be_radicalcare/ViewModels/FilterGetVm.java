package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Vehicle;
import lombok.Builder;

import java.util.List;


@Builder
public record FilterGetVm(
        String chassisNumber,
        String vehicleName,
        String segment,
        String categoryName,
        String color,
        boolean sold,
        String price,
        List<String> imageUrls
) {
    public static FilterGetVm from(Vehicle vehicle) {
        return FilterGetVm.builder()
                .chassisNumber(vehicle.getChassisNumber())
                .vehicleName(vehicle.getVehicleName())
                .segment(vehicle.getSegment())
                .categoryName(vehicle.getCategoryId() != null ? vehicle.getCategoryId().getCategoryName() : null)
                .color(vehicle.getColor())
                .sold(vehicle.getSold())
                .price(vehicle.getCostId() != null
                        ? String.format("%,.0f VNĐ", vehicle.getCostId().getBaseCost())
                        : "0")
                .imageUrls(vehicle.getImageUrls())
                .build();
    }
}
