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

        // Tạo Appointment với ngày tạo mặc định là ngày hiện tại
        Appointment appointment = Appointment.builder()
                .dateCreated(LocalDate.now()) // Ngày tạo phiếu
                .status("Pending")
                .customer(customer)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Lấy một ngày chung cho tất cả dịch vụ, nếu không có sẽ sử dụng ngày hiện tại
        LocalDate commonServiceDate = appointmentPostVm.serviceDates() != null && !appointmentPostVm.serviceDates().isEmpty()
                ? appointmentPostVm.serviceDates().get(0) // Lấy ngày đầu tiên trong danh sách serviceDates (nếu có)
                : LocalDate.now(); // Nếu không có, dùng ngày hiện tại làm mặc định

        // Lưu chi tiết các dịch vụ với ngày chung
        Double totalAmount = saveAppointmentDetails(
                appointmentPostVm.serviceIds(),
                savedAppointment,
                commonServiceDate // Sử dụng ngày chung cho tất cả dịch vụ
        );

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

        // Xóa chi tiết dịch vụ cũ và lưu mới với ngày chung
        appointmentDetailRepository.deleteAll(existingAppointment.getAppointmentDetails());
        saveAppointmentDetails(
                appointmentPostVm.serviceIds(),
                existingAppointment,
                appointmentPostVm.serviceDates() != null && !appointmentPostVm.serviceDates().isEmpty()
                        ? appointmentPostVm.serviceDates().get(0) // Lấy ngày chung cho tất cả dịch vụ
                        : LocalDate.now()
        );

        appointmentRepository.save(existingAppointment);
    }

    public void deleteAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        appointmentDetailRepository.deleteAll(appointment.getAppointmentDetails());
        appointmentRepository.delete(appointment);
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

    private Double saveAppointmentDetails(List<Long> serviceIds, Appointment appointment, LocalDate serviceDate) {
        // Kiểm tra số lượng serviceIds và serviceDates có khớp không
        if (serviceIds == null || serviceIds.isEmpty()) {
            throw new IllegalArgumentException("Service IDs cannot be empty.");
        }

        // Lưu chi tiết dịch vụ vào danh sách
        List<AppointmentDetail> details = serviceIds.stream()
                .map(serviceId -> {
                    MotorService service = motorServicesRepository.findById(serviceId)
                            .orElseThrow(() -> new IllegalArgumentException("Service not found"));

                    return AppointmentDetail.builder()
                            .appointment(appointment)
                            .motorService(service)
                            .serviceDate(serviceDate) // Gán ngày chung cho tất cả các dịch vụ
                            .serviceCost(service.getCostId().getBaseCost())
                            .description(service.getServiceName())
                            .build();
                })
                .collect(Collectors.toList());

        appointmentDetailRepository.saveAll(details);

        // Tính tổng chi phí của tất cả các dịch vụ
        return details.stream()
                .mapToDouble(AppointmentDetail::getServiceCost)
                .sum();
    }
}
