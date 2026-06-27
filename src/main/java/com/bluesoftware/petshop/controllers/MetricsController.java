package com.bluesoftware.petshop.controllers;

import com.bluesoftware.petshop.dtos.DashboardMetricsDTO;
import com.bluesoftware.petshop.repositories.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/metrics")
@PreAuthorize("hasRole('ADMIN')")
public class MetricsController {

    private final UserRepository userRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final ProductRepository productRepository;
    private final AppointmentRepository appointmentRepository;
    private final OrderRepository orderRepository;

    public MetricsController(UserRepository userRepository,
                             VeterinarianRepository veterinarianRepository,
                             ProductRepository productRepository,
                             AppointmentRepository appointmentRepository,
                             OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.productRepository = productRepository;
        this.appointmentRepository = appointmentRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics() {
        long totalUsers = userRepository.countByActiveTrue();
        long totalVets = veterinarianRepository.countByApprovedTrue();
        long totalProducts = productRepository.countByActiveTrue();
        long totalAppointments = appointmentRepository.countByActiveTrue();
        long totalOrders = orderRepository.countByActiveTrue();
        Double totalRevenue = orderRepository.totalRevenue();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfYear = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<DashboardMetricsDTO.MonthlyCount> appointmentsByMonth =
            appointmentRepository.countByMonth(startOfYear, now)
                .stream()
                .map(row -> new DashboardMetricsDTO.MonthlyCount((String) row[0], (long) row[1]))
                .collect(Collectors.toList());

        List<DashboardMetricsDTO.MonthlyTotal> salesByMonth =
            orderRepository.totalByMonth(startOfYear, now)
                .stream()
                .map(row -> new DashboardMetricsDTO.MonthlyTotal((String) row[0], (Double) row[1]))
                .collect(Collectors.toList());

        DashboardMetricsDTO metrics = DashboardMetricsDTO.builder()
                .totalUsers(totalUsers)
                .totalVeterinarians(totalVets)
                .totalProducts(totalProducts)
                .totalAppointments(totalAppointments)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .appointmentsByMonth(appointmentsByMonth)
                .salesByMonth(salesByMonth)
                .build();

        return ResponseEntity.ok(metrics);
    }
}
