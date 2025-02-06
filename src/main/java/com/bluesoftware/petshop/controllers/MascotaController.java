package com.bluesoftware.petshop.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bluesoftware.petshop.entities.Mascota;
import com.bluesoftware.petshop.services.MascotaService;

@RestController
@RequestMapping("/api/mascotas") // Ajustar el endpoint para que sea más específico
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    // Obtener todas las mascotas
    @GetMapping
    public List<Mascota> getAllMascotas() {
        return mascotaService.getAllMascotas();
    }

    // Obtener una mascota por ID
    @GetMapping("/{id}")
    public ResponseEntity<Mascota> getMascotaById(@PathVariable Integer id) {
        return mascotaService.getMascotaById(id)
                .map(ResponseEntity::ok) // Si existe, retorna 200 OK
                .orElse(ResponseEntity.notFound().build()); // Si no, retorna 404 Not Found
    }

    // Crear una nueva mascota
    @PostMapping
    public ResponseEntity<Mascota> createMascota(@RequestBody Mascota mascota) {
        Mascota nuevaMascota = mascotaService.saveMascota(mascota);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMascota); // Retorna 201 Created
    }

    // Eliminar una mascota por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMascota(@PathVariable Integer id) {
        if (mascotaService.deleteMascota(id)) { // Cambiar a booleano para verificar si existía
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si se eliminó
        }
        return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no existía
    }
}
