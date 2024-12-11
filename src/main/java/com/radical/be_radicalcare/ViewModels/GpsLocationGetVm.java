package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.GpsLocation;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GpsLocationGetVm(
        Long id,
        double latitude,
        double longitude,
        LocalDateTime timestamp,
        String customerId,
        String userId
) {
    public static GpsLocationGetVm from(GpsLocation gpsLocation) {
        return GpsLocationGetVm.builder()
                .id(gpsLocation.getId())
                .latitude(gpsLocation.getLatitude())
                .longitude(gpsLocation.getLongitude())
                .timestamp(gpsLocation.getTimestamp())
                .customerId(gpsLocation.getCustomer() != null ? gpsLocation.getCustomer().getId() : null)
                .userId(gpsLocation.getUser() != null ? gpsLocation.getUser().getId() : null)
                .build();
    }
}
