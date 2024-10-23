package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.persistence.model.Product;
import org.example.persistence.repository.ProductRepository;
import org.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repository;

    @Override
    public Optional<Product> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public Product buyProduct(Long id, int amount) {
        // Select without a lock will fail
//        Optional<Product> productOpt = repository.findById(id);

        Optional<Product> productOpt = repository.findByIdWithLock(id);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Can't buy this product.");
        }
        var product = productOpt.get();
        product.decreaseQuantity(amount);
        return repository.save(product);
    }
}
