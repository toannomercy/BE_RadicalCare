package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IFilterRepository extends JpaRepository<Vehicle, String>, JpaSpecificationExecutor<Vehicle> {
}
