package org.example.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    private Long quantity;

    private String name;

    // Version field is a good solution which could help to prevent Lost updates
    // https://vladmihalcea.com/optimistic-locking-version-property-jpa-hibernate/
    @Version
    private Long version;

    public void decreaseQuantity(int amount) {
        this.quantity -= amount;
    }

}
