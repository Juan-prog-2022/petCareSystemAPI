package com.bluesoftware.petshop.repositories;

import com.bluesoftware.petshop.entities.VetSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface VetScheduleRepository extends JpaRepository<VetSchedule, Integer> {
    List<VetSchedule> findByVeterinarianIdAndActiveTrue(Integer veterinarianId);
    List<VetSchedule> findByVeterinarianIdAndDayOfWeekAndActiveTrue(Integer veterinarianId, DayOfWeek dayOfWeek);
    List<VetSchedule> findByVeterinarian_ApprovedTrueAndActiveTrue();
}
