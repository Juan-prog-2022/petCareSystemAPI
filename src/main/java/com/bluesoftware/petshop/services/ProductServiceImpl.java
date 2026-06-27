package com.bluesoftware.petshop.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bluesoftware.petshop.entities.Product;
import com.bluesoftware.petshop.entities.ProductCategory;
import com.bluesoftware.petshop.exceptions.ResourceNotFoundException;
import com.bluesoftware.petshop.repositories.ProductRepository;

@Service
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .filter(Product::isActive)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getProductsByVetId(Integer vetId) {
        return productRepository.findByVeterinarianId(vetId)
                .stream()
                .filter(Product::isActive)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getProductsByVetIdAndCategory(Integer vetId, ProductCategory category) {
        return productRepository.findByVeterinarianIdAndCategory(vetId, category)
                .stream()
                .filter(Product::isActive)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));
    }

    @Transactional
    @Override
    public Product createProduct(Product product) {
        product.setActive(true);
        return productRepository.save(product);
    }

    @Transactional
    @Override
    public Product updateProduct(Integer id, Product productData) {

        Product product = getProductById(id);

        product.setName(productData.getName());
        product.setDescription(productData.getDescription());
        product.setPrice(productData.getPrice());
        product.setStock(productData.getStock());
        product.setCategory(productData.getCategory());

        return productRepository.save(product);
    }

    @Transactional
    @Override
    public void deleteProduct(Integer id) {

        Product product = getProductById(id);

        product.setActive(false);

        productRepository.save(product);
    }
}