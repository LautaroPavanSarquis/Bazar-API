package com.lautaro.bazar_api.controller;

import com.lautaro.bazar_api.dto.request.SaleRequestDTO;
import com.lautaro.bazar_api.dto.response.SaleByDateResponseDTO;
import com.lautaro.bazar_api.dto.response.SaleProductResponseDTO;
import com.lautaro.bazar_api.dto.response.SaleResponseDTO;
import com.lautaro.bazar_api.service.SaleService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> getAll() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @PostMapping
    public ResponseEntity<SaleResponseDTO> create(@Valid @RequestBody SaleRequestDTO request) {
        SaleResponseDTO created = saleService.createSale(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> update(@PathVariable Long id, @Valid @RequestBody SaleRequestDTO request) {
        return ResponseEntity.ok(saleService.updateSale(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<SaleProductResponseDTO>> getProductsInSale(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getProductsBySaleId(id));
    }

    @GetMapping("/by-date/{date}")
    public ResponseEntity<SaleByDateResponseDTO> getSalesSummaryByDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(saleService.getSalesSummaryByDate(date));
    }

    @GetMapping("/top")
    public ResponseEntity<SaleResponseDTO> getTopSale() {
        return ResponseEntity.ok(saleService.getTopSale());
    }
}

