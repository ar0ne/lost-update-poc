package org.example.service;

import org.example.persistence.model.Product;

import java.util.Optional;

public interface ProductService {

    Optional<Product> findById(Long id);

    Product buyProduct(Long id, int amount);

}
