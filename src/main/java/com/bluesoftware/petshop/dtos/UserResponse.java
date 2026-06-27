package com.bluesoftware.petshop.dtos;

import com.bluesoftware.petshop.entities.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Integer id;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}
