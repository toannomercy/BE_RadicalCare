package com.radical.be_radicalcare.ViewModels;


import com.radical.be_radicalcare.Entities.CostTable;
import com.radical.be_radicalcare.Entities.MotorService;
import lombok.Builder;

@Builder
public record MotorServicePostVm(String name, String description, Long costId) {
    public MotorService toMotorService() {
        MotorService mts = new MotorService();
        mts.setServiceName(name);
        mts.setServiceDescription(description);
        mts.setIsDeleted(false);
        if (costId != null) {
            CostTable costTable = new CostTable();
            costTable.setCostId(this.costId);
            mts.setCostId(costTable);
        }
        return mts;
    }
}
