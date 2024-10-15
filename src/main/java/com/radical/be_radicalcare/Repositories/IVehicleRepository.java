package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IVehicleRepository extends JpaRepository<Vehicle, String> {

}
