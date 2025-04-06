package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Entities.VehicleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IVehicleImageRepository extends JpaRepository<VehicleImage, Long> {
    @Query("SELECT vi FROM VehicleImage vi WHERE vi.chassisNumber.chassisNumber = :chassisNumber")
    List<VehicleImage> findByVehicleId(String chassisNumber);
}
