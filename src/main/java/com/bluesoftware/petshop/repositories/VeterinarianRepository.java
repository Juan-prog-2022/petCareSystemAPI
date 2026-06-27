package com.bluesoftware.petshop.repositories;

import com.bluesoftware.petshop.entities.Veterinarian;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarianRepository extends JpaRepository<Veterinarian, Integer> {
    Optional<Veterinarian> findByUserId(Integer userId);
    Optional<Veterinarian> findByMatricula(String matricula);
    Optional<Veterinarian> findBySlug(String slug);
    List<Veterinarian> findByApprovedFalse();
    boolean existsByMatricula(String matricula);
    boolean existsBySlug(String slug);
    long countByApprovedTrue();
}
