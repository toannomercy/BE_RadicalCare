package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IVehicleRepository extends
        JpaRepository<Vehicle, String>,
        PagingAndSortingRepository<Vehicle, String>,
        JpaSpecificationExecutor<Vehicle> {
}

