package com.bluesoftware.petshop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PetSpecies species;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private DogBreed breed;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    private LocalDate birthDate;

    @Size(max = 50)
    @Column(length = 50)
    private String color;

    @Positive(message = "Weight must be greater than 0")
    private Double weight; // kg

    @Size(max = 255)
    @Column(length = 255)
    private String notes;

    @Size(max = 512)
    @Column(length = 512)
    private String photoUrl;

    // 🔹 Soft delete
    @Column(nullable = false)
    private boolean active = true;

    // 🔹 Auditoría
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 🔹 Relación con Customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}