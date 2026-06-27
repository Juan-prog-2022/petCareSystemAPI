package com.bluesoftware.petshop.controllers;

import com.bluesoftware.petshop.dtos.*;
import com.bluesoftware.petshop.entities.Customer;
import com.bluesoftware.petshop.entities.Pet;
import com.bluesoftware.petshop.repositories.CustomerRepository;
import com.bluesoftware.petshop.repositories.PetRepository;
import com.bluesoftware.petshop.services.IPetService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final IPetService petService;
    private final CustomerRepository customerRepository;
    private final PetRepository petRepository;

    public PetController(IPetService petService,
                         CustomerRepository customerRepository,
                         PetRepository petRepository) {
        this.petService = petService;
        this.customerRepository = customerRepository;
        this.petRepository = petRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    public ResponseEntity<PetResponseDTO> createPet(
            @Valid @RequestBody PetRequestDTO request) {

        Pet pet = mapToEntity(request);

        Pet saved = petService.createPet(pet, request.getCustomerId());

        return ResponseEntity.ok(mapToResponse(saved));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    public ResponseEntity<List<PetResponseDTO>> getAllPets() {

        List<PetResponseDTO> response = petService.getAllPets()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN', 'CUSTOMER')")
    public ResponseEntity<PetResponseDTO> getPetById(@PathVariable Integer id) {

        Pet pet = petService.getPetById(id);

        return ResponseEntity.ok(mapToResponse(pet));
    }

    @PostMapping("/my-pets")
    public ResponseEntity<PetResponseDTO> createMyPet(
            @Valid @RequestBody CreateMyPetRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Pet pet = new Pet();
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setGender(request.getGender());
        pet.setBirthDate(request.getBirthDate());
        pet.setColor(request.getColor());
        pet.setWeight(request.getWeight());
        pet.setNotes(request.getNotes());
        pet.setPhotoUrl(request.getPhotoUrl());

        Pet saved = petService.createPet(pet, customer.getId());

        return ResponseEntity.ok(mapToResponse(saved));
    }

    @GetMapping("/my-pets")
    public ResponseEntity<List<PetResponseDTO>> getMyPets(Authentication authentication) {

        String email = authentication.getName();

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        List<Pet> pets = petRepository.findByCustomerId(customer.getId())
                .stream()
                .filter(Pet::isActive)
                .toList();

        List<PetResponseDTO> response = pets.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/my-pets/{id}")
    public ResponseEntity<PetResponseDTO> updateMyPet(
            @PathVariable Integer id,
            @Valid @RequestBody CreateMyPetRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Pet existing = petService.getPetById(id);
        if (!existing.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("No tenés permiso para modificar esta mascota");
        }

        existing.setName(request.getName());
        existing.setSpecies(request.getSpecies());
        existing.setBreed(request.getBreed());
        existing.setGender(request.getGender());
        existing.setBirthDate(request.getBirthDate());
        existing.setColor(request.getColor());
        existing.setWeight(request.getWeight());
        existing.setNotes(request.getNotes());
        existing.setPhotoUrl(request.getPhotoUrl());

        Pet updated = petService.updatePet(id, existing);

        return ResponseEntity.ok(mapToResponse(updated));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PetResponseDTO> updatePet(
            @PathVariable Integer id,
            @Valid @RequestBody PetRequestDTO request) {

        Pet pet = mapToEntity(request);

        Pet updated = petService.updatePet(id, pet);

        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    public ResponseEntity<Void> deletePet(@PathVariable Integer id) {

        petService.deletePet(id);

        return ResponseEntity.noContent().build();
    }

    private Pet mapToEntity(PetRequestDTO dto) {
        Pet pet = new Pet();

        pet.setName(dto.getName());
        pet.setSpecies(dto.getSpecies());
        pet.setBreed(dto.getBreed());
        pet.setGender(dto.getGender());
        pet.setBirthDate(dto.getBirthDate());
        pet.setColor(dto.getColor());
        pet.setWeight(dto.getWeight());
        pet.setNotes(dto.getNotes());
        pet.setPhotoUrl(dto.getPhotoUrl());

        return pet;
    }

    private PetResponseDTO mapToResponse(Pet pet) {

        PetResponseDTO dto = new PetResponseDTO();

        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setSpecies(pet.getSpecies());
        dto.setBreed(pet.getBreed());
        dto.setGender(pet.getGender());
        dto.setBirthDate(pet.getBirthDate());
        dto.setColor(pet.getColor());
        dto.setWeight(pet.getWeight());
        dto.setNotes(pet.getNotes());
        dto.setPhotoUrl(pet.getPhotoUrl());

        dto.setCustomerId(pet.getCustomer().getId());
        dto.setCustomerName(pet.getCustomer().getName());

        return dto;
    }
}
