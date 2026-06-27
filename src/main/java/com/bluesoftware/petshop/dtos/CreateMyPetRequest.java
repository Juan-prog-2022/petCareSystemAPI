package com.bluesoftware.petshop.dtos;

import com.bluesoftware.petshop.entities.DogBreed;
import com.bluesoftware.petshop.entities.Gender;
import com.bluesoftware.petshop.entities.PetSpecies;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateMyPetRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private PetSpecies species;

    private DogBreed breed;

    private Gender gender;

    private LocalDate birthDate;

    @Size(max = 50)
    private String color;

    @Positive
    private Double weight;

    @Size(max = 255)
    private String notes;

    @Size(max = 512)
    private String photoUrl;
}
