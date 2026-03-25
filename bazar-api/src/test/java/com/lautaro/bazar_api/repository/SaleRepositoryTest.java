package com.lautaro.bazar_api.repository;

import com.lautaro.bazar_api.model.Customer;
import com.lautaro.bazar_api.model.Sale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SaleRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private SaleRepository saleRepository;

    private Customer savedCustomer() {
        Customer c = Customer.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .dni("12345678")
                .email("juan@test.com")
                .build();
        return em.persist(c);
    }

    @Test
    void findBySaleDate_shouldReturnSalesOnThatDate() {
        Customer customer = savedCustomer();
        LocalDate today = LocalDate.now();

        em.persist(Sale.builder().saleDate(today).total(1000.0).customer(customer).build());
        em.persist(Sale.builder().saleDate(today.plusDays(1)).total(2000.0).customer(customer).build());
        em.flush();

        List<Sale> result = saleRepository.findBySaleDate(today);

        assertEquals(1, result.size());
        assertEquals(1000.0, result.get(0).getTotal());
    }

    @Test
    void findFirstByOrderByTotalDesc_shouldReturnHighestSale() {
        Customer customer = savedCustomer();

        em.persist(Sale.builder().saleDate(LocalDate.now()).total(1000.0).customer(customer).build());
        em.persist(Sale.builder().saleDate(LocalDate.now()).total(5000.0).customer(customer).build());
        em.persist(Sale.builder().saleDate(LocalDate.now()).total(3000.0).customer(customer).build());
        em.flush();

        Optional<Sale> result = saleRepository.findFirstByOrderByTotalDesc();

        assertTrue(result.isPresent());
        assertEquals(5000.0, result.get().getTotal());
    }

    @Test
    void findFirstByOrderByTotalDesc_shouldReturnEmpty_whenNoSales() {
        Optional<Sale> result = saleRepository.findFirstByOrderByTotalDesc();
        assertTrue(result.isEmpty());
    }
}