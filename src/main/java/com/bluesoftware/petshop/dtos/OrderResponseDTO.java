package com.bluesoftware.petshop.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.bluesoftware.petshop.entities.OrderStatus;

@Data
public class OrderResponseDTO {

    private Integer id;
    private Integer customerId;
    private String customerName;
    private LocalDateTime createdAt;
    private Double total;
    private OrderStatus status;
    private String paymentStatus;
    private String mpPreferenceId;
    private String mpPaymentId;
    private List<OrderItemResponseDTO> items;
}
