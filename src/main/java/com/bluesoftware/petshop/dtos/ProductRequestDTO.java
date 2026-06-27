package com.bluesoftware.petshop.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import com.bluesoftware.petshop.entities.ProductCategory;

@Data
public class ProductRequestDTO {

    @NotBlank(message = "{product.name.required}")
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String description;

    @NotNull(message = "{product.price.required}")
    @Positive(message = "{product.price.positive}")
    private Double price;

    @NotNull(message = "{product.stock.required}")
    @Min(value = 0, message = "{product.stock.min}")
    private Integer stock;

    private ProductCategory category;

    @Size(max = 512)
    private String imageUrl;
}
