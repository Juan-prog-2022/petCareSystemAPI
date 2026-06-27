package com.bluesoftware.petshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bluesoftware.petshop.entities.Order;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerId(Integer customerId);

    long countByActiveTrue();

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.active = true")
    Double totalRevenue();

    @Query("SELECT FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM') as month, COALESCE(SUM(o.total), 0) " +
           "FROM Order o WHERE o.active = true AND o.createdAt BETWEEN :start AND :end " +
           "GROUP BY FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM') ORDER BY month")
    List<Object[]> totalByMonth(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.active = true AND o.createdAt BETWEEN :start AND :end " +
           "ORDER BY o.createdAt DESC")
    List<Order> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
