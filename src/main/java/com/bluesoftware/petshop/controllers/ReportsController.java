package com.bluesoftware.petshop.controllers;

import com.bluesoftware.petshop.entities.*;
import com.bluesoftware.petshop.repositories.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
public class ReportsController {

    private final AppointmentRepository appointmentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VeterinarianRepository veterinarianRepository;

    public ReportsController(AppointmentRepository appointmentRepository,
                             OrderRepository orderRepository,
                             UserRepository userRepository,
                             VeterinarianRepository veterinarianRepository) {
        this.appointmentRepository = appointmentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.veterinarianRepository = veterinarianRepository;
    }

    @GetMapping("/api/admin/reports/appointments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAdminAppointmentReport(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();

        List<Appointment> appointments = appointmentRepository.findByActiveTrue()
                .stream()
                .filter(a -> !a.getDateTime().isBefore(start) && !a.getDateTime().isAfter(end))
                .toList();

        List<Map<String, Object>> response = appointments.stream().map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getId());
            m.put("customerName", a.getCustomer().getName());
            m.put("petName", a.getPet().getName());
            m.put("dateTime", a.getDateTime().toString());
            m.put("status", a.getStatus().name());
            m.put("reason", a.getReason());
            if (a.getVeterinarian() != null) {
                m.put("veterinarianName", a.getVeterinarian().getUser().getName());
                m.put("veterinarianId", a.getVeterinarian().getId());
            }
            return m;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/admin/reports/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAdminSalesReport(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();

        List<Order> orders = orderRepository.findByDateRange(start, end);

        List<Map<String, Object>> response = orders.stream().map(o -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", o.getId());
            m.put("customerName", o.getCustomer().getName());
            m.put("total", o.getTotal());
            m.put("status", o.getStatus().name());
            m.put("paymentStatus", o.getPaymentStatus() != null ? o.getPaymentStatus().name() : null);
            m.put("createdAt", o.getCreatedAt().toString());
            m.put("items", o.getItems() != null ? o.getItems().size() : 0);
            return m;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/reports/my/appointments")
    public ResponseEntity<List<Map<String, Object>>> getMyAppointmentReport(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Veterinarian vet = veterinarianRepository.findByUserId(user.getId()).orElse(null);

        if (vet == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();

        List<Appointment> appointments = appointmentRepository.findByActiveTrue()
                .stream()
                .filter(a -> a.getVeterinarian() != null && a.getVeterinarian().getId().equals(vet.getId()))
                .filter(a -> !a.getDateTime().isBefore(start) && !a.getDateTime().isAfter(end))
                .toList();

        List<Map<String, Object>> response = appointments.stream().map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getId());
            m.put("customerName", a.getCustomer().getName());
            m.put("petName", a.getPet().getName());
            m.put("dateTime", a.getDateTime().toString());
            m.put("status", a.getStatus().name());
            m.put("reason", a.getReason());
            return m;
        }).toList();

        return ResponseEntity.ok(response);
    }
}
