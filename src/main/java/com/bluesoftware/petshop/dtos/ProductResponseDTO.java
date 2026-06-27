package com.bluesoftware.petshop.dtos;

import lombok.Data;

import com.bluesoftware.petshop.entities.ProductCategory;

@Data
public class ProductResponseDTO {

    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private ProductCategory category;
    private String imageUrl;
}
