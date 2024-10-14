package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.VehicleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IVehicleImageRepository extends JpaRepository<VehicleImage, Long> {
}
