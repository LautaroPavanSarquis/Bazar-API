package com.lautaro.bazar_api.repository;

import com.lautaro.bazar_api.model.Sale;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findBySaleDate(LocalDate saleDate);

    Optional<Sale> findFirstByOrderByTotalDesc();
}

