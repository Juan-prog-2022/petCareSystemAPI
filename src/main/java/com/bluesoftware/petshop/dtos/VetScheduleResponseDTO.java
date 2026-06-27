package com.bluesoftware.petshop.dtos;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class VetScheduleResponseDTO {

    private Integer id;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
