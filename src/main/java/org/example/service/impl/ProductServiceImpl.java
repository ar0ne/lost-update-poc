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

    @Transactional
    @Override
    public void buyProduct(Long id, int amount) {
        // Select without a lock will fail
//        Optional<Product> productOpt = repository.findById(id);
        Optional<Product> productOpt = repository.findByIdWithLock(id);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Can't buy this product.");
        }
        // in this case we could rely on POJO method, because we use Lock
        var product = productOpt.get();
        if (product.getQuantity() < 0) {
            throw new RuntimeException(("Out of stoke"));
        }
        product.decreaseQuantity(amount);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void buyProduct2(Long id, int amount) {
        // in this case all good, because we rely on SQL query for decreasing amount
        Optional<Product> productOpt = repository.findById(id);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Can't buy this product.");
        }
        var product = productOpt.get();
        if (product.getQuantity() < 0) {
            throw new RuntimeException(("Out of stoke"));
        }
        repository.decreaseQuantity(product.getId(), amount);

        // we can't rely on POJO (or if we need to make few modifying operations without explicit lock
//        product.decreaseQuantity(amount);
//        repository.save(product);
    }
}
