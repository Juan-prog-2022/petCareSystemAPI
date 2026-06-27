package com.bluesoftware.petshop.dtos;

import lombok.Data;

@Data
public class CustomerResponseDTO {
    private Integer id;
    private String name;
    private String phone;
    private String address;
    private String email;
}
