package org.example.persistence.repository;

import jakarta.persistence.LockModeType;
import org.example.persistence.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = """
        select p from Product p where p.id = :id
        """)
    Optional<Product> findByIdWithLock(Long id);
}
