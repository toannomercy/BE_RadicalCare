package com.radical.be_radicalcare.Services;


import com.radical.be_radicalcare.Entities.CostTable;
import com.radical.be_radicalcare.Repositories.ICostTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
@RequiredArgsConstructor
public class CostTableService {
    private final ICostTableRepository costTableRepository;

    public List<CostTable> getAllCostTable() {
        return costTableRepository.findAll();
    }

    public Optional<CostTable> getCostTableById(Long costTableId) {
        return costTableRepository.findById(costTableId);
    }

    public void addCostTable(CostTable costTable) {
        costTableRepository.save(costTable);
    }

    public void updateCostTable(CostTable costTable) {
        CostTable existingCostTable = costTableRepository.findById(costTable.getCostId())
                .orElseThrow(() -> new RuntimeException("costTableId not found"));

        existingCostTable.setDateCreated(costTable.getDateCreated());
        existingCostTable.setBaseCost(costTable.getBaseCost());

        costTableRepository.save(existingCostTable);
    }

    public void deleteCostTable(Long costTableId){costTableRepository.deleteById(costTableId);}
}
