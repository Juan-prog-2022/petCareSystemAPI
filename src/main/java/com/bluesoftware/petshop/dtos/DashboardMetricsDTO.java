package com.bluesoftware.petshop.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardMetricsDTO {
    private long totalUsers;
    private long totalVeterinarians;
    private long totalProducts;
    private long totalAppointments;
    private long totalOrders;
    private Double totalRevenue;
    private List<MonthlyCount> appointmentsByMonth;
    private List<MonthlyTotal> salesByMonth;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyCount {
        private String month;
        private long count;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyTotal {
        private String month;
        private Double total;
    }
}
