package com.lautaro.bazar_api.repository;

import com.lautaro.bazar_api.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStockLessThanAndActiveTrue(Integer stock);

    List<Product> findByActiveTrue();
}

