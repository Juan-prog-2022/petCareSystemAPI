package com.bluesoftware.petshop.controllers;

import com.bluesoftware.petshop.dtos.*;
import com.bluesoftware.petshop.entities.*;
import com.bluesoftware.petshop.repositories.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/schedules")
public class VetScheduleController {

    private final VetScheduleRepository scheduleRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public VetScheduleController(VetScheduleRepository scheduleRepository,
                                  VeterinarianRepository veterinarianRepository,
                                  UserRepository userRepository,
                                  AppointmentRepository appointmentRepository) {
        this.scheduleRepository = scheduleRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    @GetMapping("/my-schedules")
    public ResponseEntity<List<VetScheduleResponseDTO>> getMySchedules(Authentication auth) {
        Veterinarian vet = getVetFromAuth(auth);
        List<VetScheduleResponseDTO> response = scheduleRepository
                .findByVeterinarianIdAndActiveTrue(vet.getId())
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    @PutMapping("/my-schedules")
    public ResponseEntity<List<VetScheduleResponseDTO>> updateMySchedules(
            @Valid @RequestBody List<VetScheduleRequestDTO> schedules,
            Authentication auth) {

        Veterinarian vet = getVetFromAuth(auth);

        List<VetSchedule> existing = scheduleRepository.findByVeterinarianIdAndActiveTrue(vet.getId());
        existing.forEach(s -> s.setActive(false));
        scheduleRepository.saveAll(existing);

        List<VetSchedule> updated = new ArrayList<>();
        for (VetScheduleRequestDTO dto : schedules) {
            updated.add(scheduleRepository.save(VetSchedule.builder()
                    .veterinarian(vet)
                    .dayOfWeek(dto.getDayOfWeek())
                    .startTime(dto.getStartTime())
                    .endTime(dto.getEndTime())
                    .active(true)
                    .build()));
        }

        List<VetScheduleResponseDTO> response = updated.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available-vets")
    public ResponseEntity<List<AvailableVetDTO>> getAvailableVets(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();

        List<Veterinarian> vetsWithSchedule = scheduleRepository
                .findByVeterinarian_ApprovedTrueAndActiveTrue()
                .stream()
                .map(VetSchedule::getVeterinarian)
                .distinct()
                .toList();

        List<AvailableVetDTO> response = vetsWithSchedule.stream().map(v -> {
            AvailableVetDTO dto = new AvailableVetDTO();
            dto.setId(v.getId());
            dto.setName(v.getUser().getName());
            dto.setSpecialty(v.getSpecialty());
            return dto;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<AvailableSlotDTO>> getAvailableSlots(
            @RequestParam Integer vetId,
            @RequestParam String date) {

        LocalDate localDate = LocalDate.parse(date);
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();

        List<VetSchedule> schedules = scheduleRepository
                .findByVeterinarianIdAndDayOfWeekAndActiveTrue(vetId, dayOfWeek);

        if (schedules.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        VetSchedule schedule = schedules.get(0);
        LocalTime start = schedule.getStartTime();
        LocalTime end = schedule.getEndTime();

        List<LocalTime> appointmentTimes = appointmentRepository.findByActiveTrue()
                .stream()
                .filter(a -> a.getVeterinarian() != null && a.getVeterinarian().getId().equals(vetId))
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

    private Veterinarian getVetFromAuth(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return veterinarianRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));
    }

    private VetScheduleResponseDTO toResponse(VetSchedule s) {
        VetScheduleResponseDTO dto = new VetScheduleResponseDTO();
        dto.setId(s.getId());
        dto.setDayOfWeek(s.getDayOfWeek());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        return dto;
    }
}
