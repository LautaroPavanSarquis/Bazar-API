package com.lautaro.bazar_api.service;

import com.lautaro.bazar_api.dto.request.ProductRequestDTO;
import com.lautaro.bazar_api.dto.response.ProductResponseDTO;
import com.lautaro.bazar_api.exception.ResourceNotFoundException;
import com.lautaro.bazar_api.model.Product;
import com.lautaro.bazar_api.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        Product product = Product.builder()
                .name(request.getName())
                .brand(request.getBrand())
                .price(request.getPrice())
                .stock(request.getStock())
                .active(true)
                .build();

        Product saved = productRepository.save(product);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllActive() {
        return productRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + id));

        if (!product.getActive()) {
            throw new ResourceNotFoundException("Product is inactive: id=" + id);
        }

        return toResponse(product);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + id));

        if (!product.getActive()) {
            throw new ResourceNotFoundException("Product is inactive: id=" + id);
        }

        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + id));

        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getLowStock() {
        return productRepository.findByStockLessThanAndActiveTrue(5).stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductResponseDTO toResponse(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .price(product.getPrice())
                .stock(product.getStock())
                .active(product.getActive())
                .build();
    }
}

