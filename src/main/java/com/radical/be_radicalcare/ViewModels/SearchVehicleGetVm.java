package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Vehicle;
import lombok.Builder;

import java.util.List;

@Builder
public record SearchVehicleGetVm(
        String chassisNumber,  // Số khung xe
        String vehicleName,    // Tên xe
        String version,        // Phiên bản xe
        String segment,        // Phân khúc
        String color,          // Màu sắc
        List<String> imageUrls,// Danh sách URL hình ảnh
        Double cost,           // Giá xe từ CostTable
        String category        // Danh mục xe từ Category
) {
    // Ánh xạ từ Vehicle entity
    public static SearchVehicleGetVm fromVehicleEntity(Vehicle vehicle) {
        return SearchVehicleGetVm.builder()
                .chassisNumber(vehicle.getChassisNumber())
                .vehicleName(vehicle.getVehicleName())
                .version(vehicle.getVersion())
                .segment(vehicle.getSegment())
                .color(vehicle.getColor())
                .imageUrls(vehicle.getImageUrls() != null ? vehicle.getImageUrls() : null)
                .cost(vehicle.getCostId() != null ? vehicle.getCostId().getBaseCost() : null)
                .category(vehicle.getCategoryId() != null ? vehicle.getCategoryId().getCategoryName() : null)
                .build();
    }
}
