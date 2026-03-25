package com.lautaro.bazar_api.repository;

import com.lautaro.bazar_api.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

