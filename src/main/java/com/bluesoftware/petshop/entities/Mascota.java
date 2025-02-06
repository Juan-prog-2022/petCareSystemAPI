package com.bluesoftware.petshop.entities;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String nombre;

    private String especie; // Ejemplo: Perro, Gato, Ave
    private String raza; // Opcional: Si aplica a la especie
    private String genero; // Ejemplo: Macho, Hembra
    private LocalDate fechaNacimiento; // Fecha aproximada si es conocida
    private String color; // Ejemplo: Blanco, Negro, Tricolor

    private Double peso; // En kilogramos

    private String observaciones; // Notas adicionales como condiciones médicas, etc.

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false) // Llave foránea al dueño de la mascota
    private Cliente cliente;

}
