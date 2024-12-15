package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Category;
import com.radical.be_radicalcare.Entities.CostTable;
import com.radical.be_radicalcare.Entities.Supplier;
import com.radical.be_radicalcare.Entities.Vehicle;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record VehiclePostVm(
        String chassisNumber,
        String vehicleName,
        LocalDate importDate,
        String version,
        String color,
        String segment,
        String description,
        Long costId,
        Long categoryId,
        Long supplierId,
        Boolean isDeleted,
        Boolean sold) {

    public Vehicle toEntity() {
        Vehicle vehicle = new Vehicle();
        vehicle.setChassisNumber(this.chassisNumber);
        vehicle.setVehicleName(this.vehicleName);
        vehicle.setImportDate(this.importDate);
        vehicle.setVersion(this.version);
        vehicle.setColor(this.color);
        vehicle.setSegment(this.segment);
        vehicle.setDescription(this.description);
        vehicle.setIsDeleted(this.isDeleted);
        vehicle.setSold(this.sold);

        if (this.costId != null) {
            CostTable costTable = new CostTable();
            costTable.setCostId(this.costId);
            vehicle.setCostId(costTable);
        }
        if (this.categoryId != null) {
            Category category = new Category();
            category.setId(this.categoryId);
            vehicle.setCategoryId(category);
        }
        if (this.supplierId != null) {
            Supplier supplier = new Supplier();
            supplier.setSupplierId(this.supplierId);
            vehicle.setSupplierId(supplier);
        }

        return vehicle;
    }
}
