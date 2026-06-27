package com.bluesoftware.petshop.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppointmentStatusUpdateDTO {

    @NotBlank(message = "{appointment.status.required}")
    private String status;
}
