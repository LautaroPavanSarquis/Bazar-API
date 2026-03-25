package com.lautaro.bazar_api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import com.lautaro.bazar_api.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findByStockLessThanAndActiveTrue_shouldReturnLowStockActiveProducts() {
        em.persist(Product.builder().name("Jarra").brand("X").price(100.0).stock(2).active(true).build());
        em.persist(Product.builder().name("Vaso").brand("X").price(100.0).stock(10).active(true).build());
        em.flush();

        List<Product> result = productRepository.findByStockLessThanAndActiveTrue(5);

        assertEquals(1, result.size());
        assertEquals("Jarra", result.get(0).getName());
    }

    @Test
    void findByStockLessThanAndActiveTrue_shouldNotReturnInactiveProducts() {
        em.persist(Product.builder().name("Jarra").brand("X").price(100.0).stock(2).active(false).build());
        em.flush();

        List<Product> result = productRepository.findByStockLessThanAndActiveTrue(5);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByStockLessThanAndActiveTrue_shouldNotReturnProductsAtThreshold() {
        em.persist(Product.builder().name("Jarra").brand("X").price(100.0).stock(5).active(true).build());
        em.flush();

        List<Product> result = productRepository.findByStockLessThanAndActiveTrue(5);

        assertTrue(result.isEmpty());
    }
}