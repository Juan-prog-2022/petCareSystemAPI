package com.bluesoftware.petshop.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerRequestDTO {

    @NotBlank(message = "{customer.name.required}")
    @Size(max = 100, message = "{customer.name.size}")
    private String name;

    @NotBlank(message = "{customer.phone.required}")
    @Size(max = 20, message = "{customer.phone.size}")
    private String phone;

    @NotBlank(message = "{customer.address.required}")
    @Size(max = 150, message = "{customer.address.size}")
    private String address;

    @NotBlank(message = "{customer.email.required}")
    @Email(message = "{customer.email.invalid}")
    @Size(max = 100, message = "{customer.email.size}")
    private String email;
}
