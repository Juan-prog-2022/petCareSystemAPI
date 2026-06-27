package com.bluesoftware.petshop.controllers;

import com.bluesoftware.petshop.dtos.*;
import com.bluesoftware.petshop.entities.Customer;
import com.bluesoftware.petshop.services.ICustomerService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController {

    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    // 🔹 Crear cliente
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @Valid @RequestBody CustomerRequestDTO request) {

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setEmail(request.getEmail());

        Customer saved = customerService.createCustomer(customer);

        return ResponseEntity.ok(mapToResponse(saved));
    }

    // 🔹 Listar todos
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {

        List<CustomerResponseDTO> response = customerService.getAllCustomers()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // 🔹 Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Integer id) {

        Customer customer = customerService.getCustomerById(id);

        return ResponseEntity.ok(mapToResponse(customer));
    }

    // 🔹 Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Integer id,
            @Valid @RequestBody CustomerRequestDTO request) {

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setEmail(request.getEmail());

        Customer updated = customerService.updateCustomer(id, customer);

        return ResponseEntity.ok(mapToResponse(updated));
    }

    // 🔹 Eliminar (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {

        customerService.deleteCustomer(id);

        return ResponseEntity.noContent().build();
    }

    // 🔹 Mapper
    private CustomerResponseDTO mapToResponse(Customer customer) {

        CustomerResponseDTO dto = new CustomerResponseDTO();

        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setEmail(customer.getEmail());

        return dto;
    }
}