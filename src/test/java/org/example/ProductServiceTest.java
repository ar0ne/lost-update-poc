package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.persistence.model.Product;
import org.example.persistence.repository.ProductRepository;
import org.example.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductService service;

    @Test
    public void testParallelExecutionWithPessimisticLock() throws InterruptedException {

        Product someProduct = Product.builder()
                .name("Something")
                .quantity(100L)
                .build();
        repository.save(someProduct);

        var threadCount = 100;

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    service.buyProduct(someProduct.getId(), 1);
                } catch (Exception ex) {
                    log.info("Failed to buy product" ,ex);
                } finally {
                    endLatch.countDown();
                }
            }).start();;
        }
        startLatch.countDown();
        endLatch.await();

        var updatedProduct = repository.findById(someProduct.getId()).get();

        assertEquals(0L, updatedProduct.getQuantity());
    }

    @Test
    public void testParallelExecution() throws InterruptedException {

        Product someProduct = Product.builder()
                .name("Something")
                .quantity(100L)
                .build();
        repository.save(someProduct);

        var threadCount = 100;

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicInteger successfulCounter = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    service.buyProduct2(someProduct.getId(), 1);
                    successfulCounter.incrementAndGet();
                } catch (Exception ex) {
                    log.info("Failed to buy product" ,ex);
                } finally {
                    endLatch.countDown();
                }
            }).start();;
        }
        startLatch.countDown();
        endLatch.await();

        var updatedProduct = repository.findById(someProduct.getId()).get();

        assertEquals(0L, updatedProduct.getQuantity());
        assertEquals(100 - successfulCounter.get(), updatedProduct.getQuantity());

//        assertEquals(100L, updatedProduct.getVersion());
    }

}
