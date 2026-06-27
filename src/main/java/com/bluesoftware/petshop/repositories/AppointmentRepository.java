package com.bluesoftware.petshop.repositories;

import com.bluesoftware.petshop.entities.Appointment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByActiveTrue();
    List<Appointment> findByCustomerId(Integer customerId);

    long countByActiveTrue();

    @Query("SELECT FUNCTION('TO_CHAR', a.dateTime, 'YYYY-MM') as month, COUNT(a) " +
           "FROM Appointment a WHERE a.active = true AND a.dateTime BETWEEN :start AND :end " +
           "GROUP BY FUNCTION('TO_CHAR', a.dateTime, 'YYYY-MM') ORDER BY month")
    List<Object[]> countByMonth(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT FUNCTION('TO_CHAR', a.dateTime, 'YYYY-MM') as month, COUNT(a) " +
           "FROM Appointment a WHERE a.active = true AND a.veterinarian.id = :vetId " +
           "AND a.dateTime BETWEEN :start AND :end " +
           "GROUP BY FUNCTION('TO_CHAR', a.dateTime, 'YYYY-MM') ORDER BY month")
    List<Object[]> countByMonthAndVetId(@Param("vetId") Integer vetId,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);
}
