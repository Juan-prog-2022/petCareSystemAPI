package com.bluesoftware.petshop.dtos;

import com.bluesoftware.petshop.entities.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VeterinarianResponse {
    private Integer id;
    private String name;
    private String email;
    private String matricula;
    private String slug;
    private String specialty;
    private boolean approved;
    private LocalDateTime createdAt;
}
