package com.bluesoftware.petshop.controllers;

import com.bluesoftware.petshop.dtos.*;
import com.bluesoftware.petshop.entities.Appointment;
import com.bluesoftware.petshop.entities.Customer;
import com.bluesoftware.petshop.repositories.AppointmentRepository;
import com.bluesoftware.petshop.repositories.CustomerRepository;
import com.bluesoftware.petshop.services.IAppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final IAppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;

    public AppointmentController(IAppointmentService appointmentService,
                                 AppointmentRepository appointmentRepository,
                                 CustomerRepository customerRepository) {
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @Valid @RequestBody AppointmentRequestDTO request) {

        Appointment appointment = appointmentService.createAppointment(
                request.getCustomerId(),
                request.getPetId(),
                request.getVeterinarianId(),
                request.getDateTime(),
                request.getReason()
        );

        return ResponseEntity.ok(mapToResponse(appointment));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        List<AppointmentResponseDTO> response = appointmentService.getAllAppointments()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-appointments")
    public ResponseEntity<List<AppointmentResponseDTO>> getMyAppointments(Authentication authentication) {

        String email = authentication.getName();

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        List<Appointment> appointments = appointmentRepository.findByCustomerId(customer.getId())
                .stream()
                .filter(Appointment::isActive)
                .toList();

        List<AppointmentResponseDTO> response = appointments.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/my-appointments")
    public ResponseEntity<AppointmentResponseDTO> createMyAppointment(
            @Valid @RequestBody CreateMyAppointmentRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Appointment appointment = appointmentService.createAppointment(
                customer.getId(),
                request.getPetId(),
                request.getVeterinarianId(),
                request.getDateTime(),
                request.getReason()
        );

        return ResponseEntity.ok(mapToResponse(appointment));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Integer id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(mapToResponse(appointment));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateStatus(
            @PathVariable Integer id,
            @Valid @RequestBody AppointmentStatusUpdateDTO request) {

        Appointment appointment = appointmentService.updateAppointmentStatus(id, request.getStatus());
        return ResponseEntity.ok(mapToResponse(appointment));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Integer id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    private AppointmentResponseDTO mapToResponse(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setCustomerId(appointment.getCustomer().getId());
        dto.setCustomerName(appointment.getCustomer().getName());
        dto.setPetId(appointment.getPet().getId());
        dto.setPetName(appointment.getPet().getName());
        if (appointment.getVeterinarian() != null) {
            dto.setVeterinarianId(appointment.getVeterinarian().getId());
            dto.setVeterinarianName(appointment.getVeterinarian().getUser().getName());
        }
        dto.setDateTime(appointment.getDateTime());
        dto.setReason(appointment.getReason());
        dto.setStatus(appointment.getStatus());
        return dto;
    }
}
