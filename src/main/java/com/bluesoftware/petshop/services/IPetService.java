package com.bluesoftware.petshop.services;

import java.util.List;
import com.bluesoftware.petshop.entities.Pet;

public interface IPetService {

    List<Pet> getAllPets();

    Pet getPetById(Integer id);

    Pet createPet(Pet pet, Integer customerId);

    Pet updatePet(Integer id, Pet pet);

    void deletePet(Integer id);
}