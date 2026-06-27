package com.bluesoftware.petshop.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bluesoftware.petshop.entities.Customer;
import com.bluesoftware.petshop.entities.Pet;
import com.bluesoftware.petshop.exceptions.ResourceNotFoundException;
import com.bluesoftware.petshop.repositories.CustomerRepository;
import com.bluesoftware.petshop.repositories.PetRepository;

@Service
public class PetServiceImpl implements IPetService {

    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;

    public PetServiceImpl(PetRepository petRepository,
                          CustomerRepository customerRepository) {
        this.petRepository = petRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Pet> getAllPets() {
        return petRepository.findAll()
                .stream()
                .filter(Pet::isActive)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Pet getPetById(Integer id) {
        return petRepository.findById(id)
                .filter(Pet::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("pet.not.found"));
    }

    @Transactional
    @Override
    public Pet createPet(Pet pet, Integer customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("customer.not.found"));

        pet.setCustomer(customer);
        pet.setActive(true);

        return petRepository.save(pet);
    }

    @Transactional
    @Override
    public Pet updatePet(Integer id, Pet petData) {

        Pet pet = getPetById(id);

        pet.setName(petData.getName());
        pet.setSpecies(petData.getSpecies());
        pet.setBreed(petData.getBreed());
        pet.setGender(petData.getGender());
        pet.setBirthDate(petData.getBirthDate());
        pet.setColor(petData.getColor());
        pet.setWeight(petData.getWeight());
        pet.setNotes(petData.getNotes());

        return petRepository.save(pet);
    }

    @Transactional
    @Override
    public void deletePet(Integer id) {

        Pet pet = getPetById(id);

        pet.setActive(false); // soft delete

        petRepository.save(pet);
    }
}