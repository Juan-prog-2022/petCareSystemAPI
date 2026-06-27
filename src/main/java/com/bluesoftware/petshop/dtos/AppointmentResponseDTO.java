package com.bluesoftware.petshop.dtos;

import lombok.Data;

import java.time.LocalDateTime;

import com.bluesoftware.petshop.entities.AppointmentStatus;

@Data
public class AppointmentResponseDTO {

    private Integer id;
    private Integer customerId;
    private String customerName;
    private Integer petId;
    private String petName;
    private Integer veterinarianId;
    private String veterinarianName;
    private LocalDateTime dateTime;
    private String reason;
    private AppointmentStatus status;
}
