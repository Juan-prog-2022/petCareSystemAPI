package com.bluesoftware.petshop.repositories;

import com.bluesoftware.petshop.entities.Product;
import com.bluesoftware.petshop.entities.ProductCategory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategory(ProductCategory category);
    List<Product> findByVeterinarianId(Integer veterinarianId);
    List<Product> findByVeterinarianIdAndCategory(Integer veterinarianId, ProductCategory category);
    long countByActiveTrue();
}
