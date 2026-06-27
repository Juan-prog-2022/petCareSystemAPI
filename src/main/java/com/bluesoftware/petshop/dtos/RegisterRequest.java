package com.bluesoftware.petshop.dtos;

import com.bluesoftware.petshop.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    private Role role;

    @Size(max = 20)
    private String matricula;

    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-z0-9-]+$")
    private String slug;
}
