package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Entities.VehicleImage;
import com.radical.be_radicalcare.Entities.WarrantyHistory;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record VehicleGetVm(
        String chassisNumber,
        String vehicleName,
        LocalDate importDate,
        String version,
        String color,
        String segment,
        Boolean isDeleted,
        Boolean sold,
        Long costId,
        Long categoryId,
        Long supplierId,
        List<String> warrantyHistory,
        List<String> imageUrls
) {
    public static VehicleGetVm fromEntity (Vehicle vehicle) {
        return VehicleGetVm.builder()
                .chassisNumber(vehicle.getChassisNumber())
                .vehicleName(vehicle.getVehicleName())
                .importDate(vehicle.getImportDate())
                .version(vehicle.getVersion())
                .color(vehicle.getColor())
                .segment(vehicle.getSegment())
                .isDeleted(vehicle.getIsDeleted())
                .sold(vehicle.getSold())
                .costId(vehicle.getCostId() != null ? vehicle.getCostId().getCostId() : null)
                .categoryId(vehicle.getCategoryId() != null ? vehicle.getCategoryId().getId() : null)
                .supplierId(vehicle.getSupplierId() != null ? vehicle.getSupplierId().getSupplierId() : null)
                .warrantyHistory(vehicle.getWarrantyHistory().stream().map(WarrantyHistory::toString).collect(Collectors.toList()))
                .imageUrls(vehicle.getImageUrls())
                .build();
    }
}
