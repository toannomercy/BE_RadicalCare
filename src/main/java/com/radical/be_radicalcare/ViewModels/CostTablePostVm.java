package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.CostTable;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CostTablePostVm(Double baseCost, LocalDate dateCreate) {
    public CostTable toCostTable (){
        CostTable costTable = new CostTable();
        costTable.setBaseCost(this.baseCost);
        costTable.setDateCreated(this.dateCreate);
        costTable.setIsDeleted(false);
        return costTable;
    }
}
