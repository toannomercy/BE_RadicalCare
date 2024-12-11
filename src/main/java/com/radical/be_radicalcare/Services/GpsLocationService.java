package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.GpsLocation;
import com.radical.be_radicalcare.Repositories.IGpsLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GpsLocationService {

    private final IGpsLocationRepository gpsLocationRepository;

    public void saveGpsLocation(GpsLocation gpsLocation) {
        gpsLocationRepository.save(gpsLocation);
    }

    public GpsLocation getGpsLocationByUserId(String userId) {
        return gpsLocationRepository.findFirstByUserId_IdOrderByTimestampDesc(userId);
    }
}
