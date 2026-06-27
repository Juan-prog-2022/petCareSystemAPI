package com.bluesoftware.petshop.dtos;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
