package com.bluesoftware.petshop.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequestDTO {

    @NotNull(message = "{appointment.customer.id.required}")
    private Integer customerId;

    @NotNull(message = "{appointment.pet.id.required}")
    private Integer petId;

    private Integer veterinarianId;

    @NotNull(message = "{appointment.datetime.required}")
    private LocalDateTime dateTime;

    @Size(max = 255, message = "{appointment.reason.size}")
    private String reason;
}
