package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.CostTable;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CostTableGetVm(Long costId, LocalDate dateCreate, Double baseCost, Boolean isDeleted) {
    public static CostTableGetVm from(CostTable costTable){
        return CostTableGetVm.builder()
                .costId(costTable.getCostId())
                .dateCreate(costTable.getDateCreated())
                .baseCost(costTable.getBaseCost())
                .isDeleted(costTable.getIsDeleted())
                .build();
    }
}
