package com.lautaro.bazar_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lautaro.bazar_api.dto.request.SaleItemRequestDTO;
import com.lautaro.bazar_api.dto.request.SaleRequestDTO;
import com.lautaro.bazar_api.model.Customer;
import com.lautaro.bazar_api.model.Product;
import com.lautaro.bazar_api.repository.CustomerRepository;
import com.lautaro.bazar_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SaleControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProductRepository productRepository;

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        customer = customerRepository.save(Customer.builder()
                .firstName("Juan").lastName("Pérez")
                .dni("12345678").email("juan@test.com")
                .build());

        product = productRepository.save(Product.builder()
                .name("Jarra").brand("Durex")
                .price(50_000.0).stock(10).active(true)
                .build());
    }

    @Test
    void createSale_shouldReturn201_andDiscountStock() throws Exception {
        SaleRequestDTO request = SaleRequestDTO.builder()
                .saleDate(LocalDate.of(2026, 3, 19)) // jueves, sin descuento
                .customerId(customer.getId())
                .items(List.of(SaleItemRequestDTO.builder()
                        .productId(product.getId())
                        .quantity(2)
                        .build()))
                .build();

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(100_000.0))
                .andExpect(jsonPath("$.cantidadProductos").value(2));

        Product updated = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(8, updated.getStock());
    }

    @Test
    void createSale_shouldApplyWednesdayDiscount() throws Exception {
        SaleRequestDTO request = SaleRequestDTO.builder()
                .saleDate(LocalDate.of(2026, 3, 18)) // miércoles
                .customerId(customer.getId())
                .items(List.of(SaleItemRequestDTO.builder()
                        .productId(product.getId())
                        .quantity(1)
                        .build()))
                .build();

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(42_500.0));
    }

    @Test
    void createSale_shouldReturn400_whenInsufficientStock() throws Exception {
        SaleRequestDTO request = SaleRequestDTO.builder()
                .saleDate(LocalDate.of(2026, 3, 19))
                .customerId(customer.getId())
                .items(List.of(SaleItemRequestDTO.builder()
                        .productId(product.getId())
                        .quantity(999)
                        .build()))
                .build();

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}