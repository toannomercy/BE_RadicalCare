package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Entities.VehicleImage;
import com.radical.be_radicalcare.Entities.WarrantyHistory;
import lombok.Builder;

import java.text.DecimalFormat;
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
        String price,
        Long categoryId,
        Long supplierId,
        String description,
        List<String> warrantyHistory,
        List<String> imageUrls
) {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");
    public static VehicleGetVm fromEntity(Vehicle vehicle) {
        return VehicleGetVm.builder()
                .chassisNumber(vehicle.getChassisNumber())
                .vehicleName(vehicle.getVehicleName())
                .importDate(vehicle.getImportDate())
                .version(vehicle.getVersion())
                .color(vehicle.getColor())
                .segment(vehicle.getSegment())
                .description(vehicle.getDescription())
                .isDeleted(vehicle.getIsDeleted())
                .sold(vehicle.getSold())
                .costId(vehicle.getCostId() != null ? vehicle.getCostId().getCostId() : null)
                .price(vehicle.getCostId() != null ?
                        DECIMAL_FORMAT.format(vehicle.getCostId().getPrice()) + " VNĐ" : null)
                .categoryId(vehicle.getCategoryId() != null ? vehicle.getCategoryId().getId() : null)
                .supplierId(vehicle.getSupplierId() != null ? vehicle.getSupplierId().getSupplierId() : null)
                .warrantyHistory(vehicle.getWarrantyHistory() != null
                        ? vehicle.getWarrantyHistory().stream().map(WarrantyHistory::toString).collect(Collectors.toList())
                        : null)
                .imageUrls(vehicle.getImageUrls())
                .build();
    }
}
