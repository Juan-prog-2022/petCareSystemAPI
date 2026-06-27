package com.bluesoftware.petshop.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

import com.bluesoftware.petshop.entities.*;

@Data
public class PetRequestDTO {

    @NotBlank(message = "{pet.name.required}")
    @Size(max = 100)
    private String name;

    @NotNull(message = "{pet.species.required}")
    private PetSpecies species;

    private DogBreed breed;

    private Gender gender;

    private LocalDate birthDate;

    @Size(max = 50)
    private String color;

    @Positive(message = "{pet.weight.positive}")
    private Double weight;

    @Size(max = 255)
    private String notes;

    @Size(max = 512)
    private String photoUrl;

    @NotNull(message = "{pet.customer.required}")
    private Integer customerId;
}
