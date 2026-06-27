package com.bluesoftware.petshop.dtos;

import lombok.Data;

import java.time.LocalDate;

import com.bluesoftware.petshop.entities.*;

@Data
public class PetResponseDTO {

    private Integer id;
    private String name;
    private PetSpecies species;
    private DogBreed breed;
    private Gender gender;
    private LocalDate birthDate;
    private String color;
    private Double weight;
    private String notes;
    private String photoUrl;

    private Integer customerId;
    private String customerName;
}
