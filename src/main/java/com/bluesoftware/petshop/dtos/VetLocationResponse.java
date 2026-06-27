package com.bluesoftware.petshop.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VetLocationResponse {
    private Integer id;
    private String name;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private String slug;
}
