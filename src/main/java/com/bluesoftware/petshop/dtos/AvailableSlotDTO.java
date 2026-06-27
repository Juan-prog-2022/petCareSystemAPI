package com.bluesoftware.petshop.dtos;

import lombok.Data;
import java.time.LocalTime;

@Data
public class AvailableSlotDTO {
    private LocalTime time;
    private boolean available;
}
