package com.bluesoftware.petshop.controllers;

import com.bluesoftware.petshop.dtos.VeterinarianResponse;
import com.bluesoftware.petshop.entities.User;
import com.bluesoftware.petshop.entities.Veterinarian;
import com.bluesoftware.petshop.exceptions.BadRequestException;
import com.bluesoftware.petshop.exceptions.ResourceNotFoundException;
import com.bluesoftware.petshop.repositories.UserRepository;
import com.bluesoftware.petshop.repositories.VeterinarianRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final VeterinarianRepository veterinarianRepository;
    private final UserRepository userRepository;

    public AdminController(VeterinarianRepository veterinarianRepository,
                           UserRepository userRepository) {
        this.veterinarianRepository = veterinarianRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/veterinarians/pending")
    public ResponseEntity<List<VeterinarianResponse>> listPendingVeterinarians() {
        List<VeterinarianResponse> response = veterinarianRepository.findByApprovedFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/veterinarians/{id}/approve")
    public ResponseEntity<VeterinarianResponse> approveVeterinarian(@PathVariable Integer id) {
        Veterinarian vet = veterinarianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario no encontrado"));

        User user = vet.getUser();
        user.setActive(true);
        userRepository.save(user);

        vet.setApproved(true);
        veterinarianRepository.save(vet);

        return ResponseEntity.ok(mapToResponse(vet));
    }

    @DeleteMapping("/veterinarians/{id}/reject")
    public ResponseEntity<Void> rejectVeterinarian(@PathVariable Integer id) {
        Veterinarian vet = veterinarianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario no encontrado"));

        veterinarianRepository.delete(vet);
        userRepository.delete(vet.getUser());

        return ResponseEntity.noContent().build();
    }

    private VeterinarianResponse mapToResponse(Veterinarian vet) {
        VeterinarianResponse response = new VeterinarianResponse();
        response.setId(vet.getId());
        response.setName(vet.getUser().getName());
        response.setEmail(vet.getUser().getEmail());
        response.setMatricula(vet.getMatricula());
        response.setSlug(vet.getSlug());
        response.setSpecialty(vet.getSpecialty());
        response.setApproved(vet.isApproved());
        response.setCreatedAt(vet.getCreatedAt());
        return response;
    }
}
