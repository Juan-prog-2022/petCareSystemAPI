package com.bluesoftware.petshop.dtos;

import lombok.Data;

@Data
public class OrderItemResponseDTO {

    private Integer productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double subtotal;
}
