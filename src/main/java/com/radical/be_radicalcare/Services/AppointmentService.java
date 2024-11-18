package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.Appointment;
import com.radical.be_radicalcare.Entities.AppointmentDetail;
import com.radical.be_radicalcare.Entities.Customer;
import com.radical.be_radicalcare.Entities.MotorService;
import com.radical.be_radicalcare.Repositories.IAppointmentDetailRepository;
import com.radical.be_radicalcare.Repositories.IAppointmentRepository;
import com.radical.be_radicalcare.Repositories.ICustomerRepository;
import com.radical.be_radicalcare.Repositories.IMotorServicesRepository;
import com.radical.be_radicalcare.ViewModels.AppointmentPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class AppointmentService {
    private final IAppointmentRepository appointmentRepository;
    private final ICustomerRepository customerRepository;
    private final IMotorServicesRepository motorServicesRepository;
    private final IAppointmentDetailRepository appointmentDetailRepository;

    public void getAllAppointments() {
        appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    public void addAppointment(AppointmentPostVm appointmentPostVm) {
        Appointment appointment = appointmentPostVm.toEntity();

        Customer customer = customerRepository.findById(appointmentPostVm.customerId())
                .orElseThrow(()-> new ResourceNotFoundException("Customer not found"));
        appointment.setCustomer(customer);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        saveServices(appointmentPostVm, savedAppointment);
    }

    public void updateAppointment(Long appointmentId, AppointmentPostVm appointmentPostVm) {
        Appointment existingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        existingAppointment.setDateCreated(appointmentPostVm.dateCreated());

        Customer customer = customerRepository.findById(appointmentPostVm.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        existingAppointment.setCustomer(customer);

        appointmentDetailRepository.deleteAll(existingAppointment.getAppointmentDetails());

        saveServices(appointmentPostVm, existingAppointment);
    }

    private void saveServices(AppointmentPostVm appointmentPostVm, Appointment existingAppointment) {
        List<AppointmentDetail> appointmentDetails = appointmentPostVm.serviceIds().stream().map(serviceId -> {
            MotorService service = motorServicesRepository.findById(serviceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
            AppointmentDetail appointmentDetail = new AppointmentDetail();
            appointmentDetail.setMotorService(service);
            appointmentDetail.setAppointment(existingAppointment);
            appointmentDetail.setServiceDate(LocalDate.now());
            appointmentDetail.setServiceCost(service.getCostId().getBaseCost());
            appointmentDetail.setDescription(service.getServiceName());
            return appointmentDetail;
        }).collect(Collectors.toList());

        existingAppointment.setAppointmentDetails(appointmentDetails);
        appointmentRepository.save(existingAppointment);

        appointmentDetailRepository.saveAll(appointmentDetails);
    }


    public void deleteAppointment(Long appointmentId) {
        Appointment existingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        appointmentDetailRepository.deleteAll(existingAppointment.getAppointmentDetails());

        appointmentRepository.delete(existingAppointment);
    }

}
