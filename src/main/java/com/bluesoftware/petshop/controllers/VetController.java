package com.bluesoftware.petshop.controllers;

import com.bluesoftware.petshop.dtos.VetLocationUpdateRequest;
import com.bluesoftware.petshop.entities.User;
import com.bluesoftware.petshop.entities.Veterinarian;
import com.bluesoftware.petshop.repositories.UserRepository;
import com.bluesoftware.petshop.repositories.VeterinarianRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vet")
public class VetController {

    private final VeterinarianRepository veterinarianRepository;
    private final UserRepository userRepository;

    public VetController(VeterinarianRepository veterinarianRepository,
                         UserRepository userRepository) {
        this.veterinarianRepository = veterinarianRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/my-profile")
    public ResponseEntity<?> getMyProfile(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Veterinarian vet = veterinarianRepository.findByUserId(user.getId())
                .orElse(null);

        if (vet == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", vet.getId());
        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("matricula", vet.getMatricula());
        profile.put("slug", vet.getSlug());
        profile.put("specialty", vet.getSpecialty());
        profile.put("address", vet.getAddress());
        profile.put("city", vet.getCity());
        profile.put("latitude", vet.getLatitude());
        profile.put("longitude", vet.getLongitude());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/location")
    public ResponseEntity<?> updateLocation(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestBody VetLocationUpdateRequest request) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Veterinarian vet = veterinarianRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));

        vet.setAddress(request.getAddress());
        vet.setCity(request.getCity());
        vet.setLatitude(request.getLatitude());
        vet.setLongitude(request.getLongitude());
        veterinarianRepository.save(vet);

        Map<String, Object> response = new HashMap<>();
        response.put("id", vet.getId());
        response.put("address", vet.getAddress());
        response.put("city", vet.getCity());
        response.put("latitude", vet.getLatitude());
        response.put("longitude", vet.getLongitude());
        return ResponseEntity.ok(response);
    }
}
