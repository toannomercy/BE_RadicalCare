package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.GpsLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGpsLocationRepository extends JpaRepository<GpsLocation, Long> {
    GpsLocation findFirstByUserId_IdOrderByTimestampDesc(String userId);
}
