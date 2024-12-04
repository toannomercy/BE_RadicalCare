package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.MotorService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IMotorServicesRepository extends JpaRepository<MotorService, Long>, JpaSpecificationExecutor<MotorService> {
}
