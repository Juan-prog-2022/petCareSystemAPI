package com.bluesoftware.petshop.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {

    @NotNull
    private Integer customerId;

    @NotEmpty
    private List<OrderItemDTO> items;
}
