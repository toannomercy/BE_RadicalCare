package com.radical.be_radicalcare.ViewModels;


import com.radical.be_radicalcare.Entities.MotorService;
import lombok.Builder;

@Builder
public record MotorServicePostVm(String ServiceName, String ServiceDescription) {
    public MotorService toMotorService() {
        MotorService mts = new MotorService();
        mts.setServiceName(ServiceName);
        mts.setServiceDescription(ServiceDescription);
        return mts;
    }
}
