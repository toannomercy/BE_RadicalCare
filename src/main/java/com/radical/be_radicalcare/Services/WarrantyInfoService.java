package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.WarrantyInfo;
import com.radical.be_radicalcare.Repositories.IWarrantyInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
@RequiredArgsConstructor
public class WarrantyInfoService {
    private final IWarrantyInfoRepository warrantyInfoRepository;

    public List<WarrantyInfo> getAllWarrantyInfos() {
        return warrantyInfoRepository.findAll();
    }

    public Optional<WarrantyInfo> getWarrantyInfoById(Long warrantyInfoId) {
        return warrantyInfoRepository.findById(warrantyInfoId);
    }

    public void addWarrantyInfo(WarrantyInfo warrantyInfo) {
        warrantyInfoRepository.save(warrantyInfo);
    }

    public void updateWarrantyInfo(WarrantyInfo warrantyInfo) {
        WarrantyInfo existingWarrantyInfo = warrantyInfoRepository.findById(warrantyInfo.getId())
                .orElseThrow(() -> new RuntimeException("WarrantyInfo not found"));

        existingWarrantyInfo.setWarrantyType(warrantyInfo.getWarrantyType());
        existingWarrantyInfo.setWarrantyDescription(warrantyInfo.getWarrantyDescription());

        warrantyInfoRepository.save(existingWarrantyInfo);
    }

    public void deleteWarrantyInfoById(Long warrantyInfoId) {
        warrantyInfoRepository.deleteById(warrantyInfoId);
    }
}
