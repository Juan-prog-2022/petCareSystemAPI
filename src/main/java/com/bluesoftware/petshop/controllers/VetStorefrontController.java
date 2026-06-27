package com.bluesoftware.petshop.controllers;

import com.bluesoftware.petshop.dtos.*;
import com.bluesoftware.petshop.entities.*;
import com.bluesoftware.petshop.repositories.*;
import com.bluesoftware.petshop.services.IProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/vet")
public class VetStorefrontController {

    private final VeterinarianRepository veterinarianRepository;
    private final VetScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final IProductService productService;

    public VetStorefrontController(VeterinarianRepository veterinarianRepository,
                                    VetScheduleRepository scheduleRepository,
                                    AppointmentRepository appointmentRepository,
                                    IProductService productService) {
        this.veterinarianRepository = veterinarianRepository;
        this.scheduleRepository = scheduleRepository;
        this.appointmentRepository = appointmentRepository;
        this.productService = productService;
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getVetProfile(@PathVariable String slug) {
        Veterinarian vet = veterinarianRepository.findBySlug(slug)
                .filter(Veterinarian::isApproved)
                .orElse(null);

        if (vet == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", vet.getId());
        profile.put("name", vet.getUser().getName());
        profile.put("specialty", vet.getSpecialty());
        profile.put("matricula", vet.getMatricula());
        profile.put("slug", vet.getSlug());
        profile.put("address", vet.getAddress());
        profile.put("city", vet.getCity());
        profile.put("latitude", vet.getLatitude());
        profile.put("longitude", vet.getLongitude());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{slug}/products")
    public ResponseEntity<List<ProductResponseDTO>> getVetProducts(
            @PathVariable String slug,
            @RequestParam(required = false) String category) {

        Veterinarian vet = veterinarianRepository.findBySlug(slug)
                .filter(Veterinarian::isApproved)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));

        List<Product> products;
        if (category != null && !category.isBlank()) {
            try {
                ProductCategory cat = ProductCategory.valueOf(category.toUpperCase());
                products = productService.getProductsByVetIdAndCategory(vet.getId(), cat);
            } catch (IllegalArgumentException e) {
                products = productService.getProductsByVetId(vet.getId());
            }
        } else {
            products = productService.getProductsByVetId(vet.getId());
        }

        List<ProductResponseDTO> response = products.stream().map(p -> {
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setPrice(p.getPrice());
            dto.setStock(p.getStock());
            dto.setCategory(p.getCategory());
            dto.setImageUrl(p.getImageUrl());
            return dto;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{slug}/schedules")
    public ResponseEntity<List<VetScheduleResponseDTO>> getVetSchedules(@PathVariable String slug) {
        Veterinarian vet = veterinarianRepository.findBySlug(slug)
                .filter(Veterinarian::isApproved)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));

        List<VetScheduleResponseDTO> response = scheduleRepository
                .findByVeterinarianIdAndActiveTrue(vet.getId())
                .stream()
                .map(s -> {
                    VetScheduleResponseDTO dto = new VetScheduleResponseDTO();
                    dto.setId(s.getId());
                    dto.setDayOfWeek(s.getDayOfWeek());
                    dto.setStartTime(s.getStartTime());
                    dto.setEndTime(s.getEndTime());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{slug}/available-slots")
    public ResponseEntity<List<AvailableSlotDTO>> getVetAvailableSlots(
            @PathVariable String slug,
            @RequestParam String date) {

        Veterinarian vet = veterinarianRepository.findBySlug(slug)
                .filter(Veterinarian::isApproved)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));

        LocalDate localDate = LocalDate.parse(date);
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();

        List<VetSchedule> schedules = scheduleRepository
                .findByVeterinarianIdAndDayOfWeekAndActiveTrue(vet.getId(), dayOfWeek);

        if (schedules.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        VetSchedule schedule = schedules.get(0);
        LocalTime start = schedule.getStartTime();
        LocalTime end = schedule.getEndTime();

        List<LocalTime> appointmentTimes = appointmentRepository.findByActiveTrue()
                .stream()
                .filter(a -> a.getVeterinarian() != null && a.getVeterinarian().getId().equals(vet.getId()))
                .filter(a -> a.getDateTime().toLocalDate().equals(localDate))
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .map(a -> a.getDateTime().toLocalTime())
                .toList();

        List<AvailableSlotDTO> slots = new ArrayList<>();
        LocalTime cursor = start;
        while (cursor.isBefore(end)) {
            AvailableSlotDTO slot = new AvailableSlotDTO();
            slot.setTime(cursor);
            slot.setAvailable(!appointmentTimes.contains(cursor));
            slots.add(slot);
            cursor = cursor.plusMinutes(30);
        }

        return ResponseEntity.ok(slots);
    }
}
