package com.bluesoftware.petshop.dtos;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VetLocationUpdateRequest {
    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String city;

    private Double latitude;
    private Double longitude;
}
