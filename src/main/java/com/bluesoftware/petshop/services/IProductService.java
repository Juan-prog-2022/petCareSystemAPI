package com.bluesoftware.petshop.services;

import java.util.List;
import com.bluesoftware.petshop.entities.Product;
import com.bluesoftware.petshop.entities.ProductCategory;

public interface IProductService {

    List<Product> getAllProducts();

    List<Product> getProductsByVetId(Integer vetId);

    List<Product> getProductsByVetIdAndCategory(Integer vetId, ProductCategory category);

    Product getProductById(Integer id);

    Product createProduct(Product product);

    Product updateProduct(Integer id, Product product);

    void deleteProduct(Integer id);
}