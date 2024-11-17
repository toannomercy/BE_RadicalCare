package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.Appointment;
import com.radical.be_radicalcare.Entities.Customer;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record AppointmentPostVm(
    LocalDate dateCreated,
    String status,
    String customerId,
    List<Long> serviceIds
) {
    public Appointment toEntity() {
        Appointment appointment = new Appointment();
        appointment.setDateCreated(this.dateCreated);
        appointment.setStatus(this.status);

        if(this.customerId != null) {
            Customer customer = new Customer();
            customer.setId(this.customerId);
            appointment.setCustomer(customer);
        }

        return appointment;
    }
}
