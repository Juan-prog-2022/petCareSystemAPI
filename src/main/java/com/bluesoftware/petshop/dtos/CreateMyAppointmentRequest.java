package com.bluesoftware.petshop.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateMyAppointmentRequest {

    @NotNull
    private Integer petId;

    private Integer veterinarianId;

    @NotNull
    private LocalDateTime dateTime;

    @Size(max = 255)
    private String reason;
}
