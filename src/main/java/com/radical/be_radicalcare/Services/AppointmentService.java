package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.Appointment;
import com.radical.be_radicalcare.Entities.AppointmentDetail;
import com.radical.be_radicalcare.Entities.Customer;
import com.radical.be_radicalcare.Entities.MotorService;
import com.radical.be_radicalcare.Repositories.IAppointmentDetailRepository;
import com.radical.be_radicalcare.Repositories.IAppointmentRepository;
import com.radical.be_radicalcare.Repositories.ICustomerRepository;
import com.radical.be_radicalcare.Repositories.IMotorServicesRepository;
import com.radical.be_radicalcare.ViewModels.AppointmentGetVm;
import com.radical.be_radicalcare.ViewModels.AppointmentPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final IAppointmentRepository appointmentRepository;
    private final ICustomerRepository customerRepository;
    private final IMotorServicesRepository motorServicesRepository;
    private final IAppointmentDetailRepository appointmentDetailRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public void addAppointment(AppointmentPostVm appointmentPostVm) {
        Customer customer = customerRepository.findById(appointmentPostVm.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Appointment appointment = Appointment.builder()
                .dateCreated(appointmentPostVm.dateCreated())
                .status("Pending")
                .customer(customer)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        Double totalAmount = saveAppointmentDetails(appointmentPostVm.serviceIds(), savedAppointment);

        savedAppointment.setTotalAmount(totalAmount);
        appointmentRepository.save(savedAppointment);
    }

    public void updateAppointment(Long appointmentId, AppointmentPostVm appointmentPostVm) {
        Appointment existingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        Customer customer = customerRepository.findById(appointmentPostVm.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        existingAppointment.setDateCreated(appointmentPostVm.dateCreated());
        existingAppointment.setCustomer(customer);

        appointmentDetailRepository.deleteAll(existingAppointment.getAppointmentDetails());
        saveAppointmentDetails(appointmentPostVm.serviceIds(), existingAppointment);

        appointmentRepository.save(existingAppointment);
    }

    public void deleteAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        appointmentDetailRepository.deleteAll(appointment.getAppointmentDetails());
        appointmentRepository.delete(appointment);
    }

    public Appointment createQuickAppointment(String customerId, List<Long> serviceIds) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Appointment appointment = Appointment.builder()
                .dateCreated(LocalDate.now())
                .status("Pending")
                .customer(customer)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        saveAppointmentDetails(serviceIds, savedAppointment);
        return savedAppointment;
    }

    public List<AppointmentGetVm> searchAppointments(Long customerId, LocalDate dateCreated, List<Long> serviceIds) {
        return appointmentRepository.findAll().stream()
                .filter(appointment -> {
                    boolean matchesCustomer = customerId == null || appointment.getCustomer().getId().equals(String.valueOf(customerId));
                    boolean matchesDate = dateCreated == null || appointment.getDateCreated().equals(dateCreated);
                    boolean matchesService = serviceIds == null || appointment.getAppointmentDetails().stream()
                            .anyMatch(detail -> serviceIds.contains(detail.getMotorService().getServiceId()));

                    return matchesCustomer && matchesDate && matchesService;
                })
                .map(AppointmentGetVm::fromEntity)
                .collect(Collectors.toList());
    }

    private Double saveAppointmentDetails(List<Long> serviceIds, Appointment appointment) {
        List<AppointmentDetail> details = serviceIds.stream()
                .map(serviceId -> {
                    MotorService service = motorServicesRepository.findById(serviceId)
                            .orElseThrow(() -> new IllegalArgumentException("Service not found"));

                    return AppointmentDetail.builder()
                            .appointment(appointment)
                            .motorService(service)
                            .serviceDate(LocalDate.now())
                            .serviceCost(service.getCostId().getBaseCost())
                            .description(service.getServiceName())
                            .build();
                })
                .toList();

        appointmentDetailRepository.saveAll(details);

        return details.stream()
                .mapToDouble(AppointmentDetail::getServiceCost)
                .sum();
    }
}
