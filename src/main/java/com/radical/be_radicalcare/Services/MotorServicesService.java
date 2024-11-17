package com.radical.be_radicalcare.Services;


import com.radical.be_radicalcare.Entities.CostTable;
import com.radical.be_radicalcare.Entities.MotorService;
import com.radical.be_radicalcare.Repositories.ICostTableRepository;
import com.radical.be_radicalcare.Repositories.IMotorServicesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class MotorServicesService {

    private final IMotorServicesRepository motorServicesRepository;
    private final ICostTableRepository costTableRepository;
    public List<MotorService> getAllMotorServices() {
        return motorServicesRepository.findAll();
    }

    public Optional<MotorService> getMotorServiceById(Long id){
        return motorServicesRepository.findById(id);
    }

    public void addMotorService (MotorService motorService){
        motorServicesRepository.save(motorService);
    }

    public void updateMotorService(MotorService motorService){
        MotorService existingMotorService = motorServicesRepository.findById(motorService.getServiceId())
                .orElseThrow(() ->new RuntimeException("MotorService not found"));

        existingMotorService.setServiceName(motorService.getServiceName());
        existingMotorService.setServiceDescription(motorService.getServiceDescription());

        CostTable costTable = costTableRepository.findById(motorService.getCostId().getCostId())
                .orElseThrow(() -> new RuntimeException("CostID not found"));
        existingMotorService.setCostId(costTable);
    }

    public void deletedMotorServiceById(Long motorServiceId) {
        motorServicesRepository.deleteById(motorServiceId);
    }
}
