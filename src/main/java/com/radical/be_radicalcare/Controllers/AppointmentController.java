package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Entities.Appointment;
import com.radical.be_radicalcare.Services.AppointmentService;
import com.radical.be_radicalcare.ViewModels.AppointmentGetVm;
import com.radical.be_radicalcare.ViewModels.AppointmentPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<?> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Appointments retrieved successfully");
        response.put("data", appointments);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Appointment retrieved successfully");
        response.put("data", appointment);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentPostVm appointmentPostVm) {
        appointmentService.addAppointment(appointmentPostVm);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 201);
        response.put("message", "Appointment created successfully");

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/quick-create")
    public ResponseEntity<?> createQuickAppointment(
            @RequestParam String customerId,
            @RequestParam List<Long> serviceIds) {
        Appointment appointment = appointmentService.createQuickAppointment(customerId, serviceIds);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 201);
        response.put("message", "Quick appointment created successfully");
        response.put("data", appointment);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAppointments(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) LocalDate dateCreated,
            @RequestParam(required = false) List<Long> serviceIds) {
        List<AppointmentGetVm> appointments = appointmentService.searchAppointments(customerId, dateCreated, serviceIds);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Appointments retrieved successfully");
        response.put("data", appointments);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentPostVm appointmentPostVm) {
        appointmentService.updateAppointment(id, appointmentPostVm);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Appointment updated successfully");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Appointment deleted successfully");

        return ResponseEntity.ok(response);
    }
}
