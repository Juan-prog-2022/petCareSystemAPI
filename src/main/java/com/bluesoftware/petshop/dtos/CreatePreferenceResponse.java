package com.bluesoftware.petshop.dtos;

import lombok.Data;

@Data
public class CreatePreferenceResponse {
    private String preferenceId;
    private String checkoutUrl;
}
