package com.radical.be_radicalcare.ViewModels;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record VehicleDtoGetVm(
        String chassisNumber,
        String vehicleName,
        LocalDate importDate,
        String version,
        String color,
        String segment,
        Boolean isDeleted,
        Boolean sold,
        Double price,
        String description,
        List<String> imageUrls
) {
    // Optional: Add conversion methods if needed
    public static VehicleDtoGetVm fromDto(com.radical.be_radicalcare.Dto.VehicleDto vehicleDto) {
        if (vehicleDto == null) {
            throw new IllegalArgumentException("VehicleDto is null and cannot be converted to VehicleDtoGetVm");
        }
        return VehicleDtoGetVm.builder()
                .chassisNumber(vehicleDto.getChassisNumber())
                .vehicleName(vehicleDto.getVehicleName())
                .importDate(vehicleDto.getImportDate())
                .version(vehicleDto.getVersion())
                .color(vehicleDto.getColor())
                .segment(vehicleDto.getSegment())
                .isDeleted(vehicleDto.getIsDeleted())
                .sold(vehicleDto.getSold())
                .price(vehicleDto.getPrice())
                .description(vehicleDto.getDescription())
                .imageUrls(vehicleDto.getImageUrls())
                .build();
    }
}
