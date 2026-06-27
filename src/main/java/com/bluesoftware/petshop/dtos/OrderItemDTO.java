package com.bluesoftware.petshop.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OrderItemDTO {

    @NotNull
    private Integer productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
