package com.bluesoftware.petshop.controllers;

import com.bluesoftware.petshop.config.TenantContext;
import com.bluesoftware.petshop.dtos.*;
import com.bluesoftware.petshop.entities.*;
import com.bluesoftware.petshop.repositories.UserRepository;
import com.bluesoftware.petshop.repositories.VeterinarianRepository;
import com.bluesoftware.petshop.services.IProductService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final IProductService productService;
    private final VeterinarianRepository veterinarianRepository;
    private final UserRepository userRepository;

    public ProductController(IProductService productService,
                              VeterinarianRepository veterinarianRepository,
                              UserRepository userRepository) {
        this.productService = productService;
        this.veterinarianRepository = veterinarianRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    @PostMapping("/my-products")
    public ResponseEntity<ProductResponseDTO> createMyProduct(
            @Valid @RequestBody ProductRequestDTO request,
            Authentication auth) {

        Veterinarian vet = getVetFromAuth(auth);

        Product product = mapToEntity(request);
        product.setVeterinarian(vet);

        Product saved = productService.createProduct(product);

        return ResponseEntity.ok(mapToResponse(saved));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    @GetMapping("/my-products")
    public ResponseEntity<List<ProductResponseDTO>> getMyProducts(Authentication auth) {

        Veterinarian vet = getVetFromAuth(auth);

        List<ProductResponseDTO> response = productService.getProductsByVetId(vet.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO request) {

        Product product = mapToEntity(request);

        Product saved = productService.createProduct(product);

        return ResponseEntity.ok(mapToResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<Product> products;

        Veterinarian tenantVet = TenantContext.getCurrentVet();
        if (tenantVet != null) {
            products = productService.getProductsByVetId(tenantVet.getId());
        } else {
            products = productService.getAllProducts();
        }

        List<ProductResponseDTO> response = products.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Integer id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(mapToResponse(product));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequestDTO request) {

        Product product = mapToEntity(request);
        Product updated = productService.updateProduct(id, product);

        return ResponseEntity.ok(mapToResponse(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    private Product mapToEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());
        product.setImageUrl(dto.getImageUrl());
        return product;
    }

    private ProductResponseDTO mapToResponse(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategory(product.getCategory());
        dto.setImageUrl(product.getImageUrl());
        return dto;
    }

    private Veterinarian getVetFromAuth(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return veterinarianRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));
    }
}
