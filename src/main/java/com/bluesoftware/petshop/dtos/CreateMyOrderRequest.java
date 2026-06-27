package com.bluesoftware.petshop.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateMyOrderRequest {

    @NotEmpty
    @Valid
    private List<OrderItemDTO> items;
}
