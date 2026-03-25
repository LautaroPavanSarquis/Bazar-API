package com.lautaro.bazar_api.service;

import com.lautaro.bazar_api.dto.request.SaleItemRequestDTO;
import com.lautaro.bazar_api.dto.request.SaleRequestDTO;
import com.lautaro.bazar_api.dto.response.SaleByDateResponseDTO;
import com.lautaro.bazar_api.dto.response.SaleResponseDTO;
import com.lautaro.bazar_api.dto.response.SaleProductResponseDTO;
import com.lautaro.bazar_api.exception.InsufficientStockException;
import com.lautaro.bazar_api.exception.ResourceNotFoundException;
import com.lautaro.bazar_api.model.Customer;
import com.lautaro.bazar_api.model.Product;
import com.lautaro.bazar_api.model.Sale;
import com.lautaro.bazar_api.model.SaleItem;
import com.lautaro.bazar_api.repository.CustomerRepository;
import com.lautaro.bazar_api.repository.ProductRepository;
import com.lautaro.bazar_api.repository.SaleRepository;
import com.lautaro.bazar_api.service.discount.DiscountStrategy;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final List<DiscountStrategy> discountStrategies;

    public SaleService(
            SaleRepository saleRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            List<DiscountStrategy> discountStrategies
    ) {
        this.saleRepository = saleRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.discountStrategies = discountStrategies;
    }

    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO request) {
        Objects.requireNonNull(request, "request must not be null");

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: id=" + request.getCustomerId()));

        Sale sale = Sale.builder()
                .saleDate(request.getSaleDate())
                .customer(customer)
                .total(0.0)
                .build();

        double subtotal = processItems(sale, request.getItems());
        double discountedTotal = applyDiscounts(subtotal, sale.getSaleDate().getDayOfWeek());
        sale.setTotal(discountedTotal);

        Sale persisted = saleRepository.save(sale);
        return toResponseFromEntity(persisted);
    }

    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getAllSales() {
        return saleRepository.findAll().stream()
                .map(this::toResponseFromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public SaleResponseDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found: id=" + id));
        return toResponseFromEntity(sale);
    }

    @Transactional
    public SaleResponseDTO updateSale(Long id, SaleRequestDTO request) {
        Objects.requireNonNull(request, "request must not be null");

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found: id=" + id));

        // Restore stock from previous items
        List<SaleItem> previousItems = new ArrayList<>(sale.getItems());
        for (SaleItem previousItem : previousItems) {
            Product product = previousItem.getProduct();
            int availableStock = product.getStock() == null ? 0 : product.getStock();
            product.setStock(availableStock + previousItem.getQuantity());
            productRepository.save(product);

            sale.removeItem(previousItem);
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: id=" + request.getCustomerId()));

        sale.setCustomer(customer);
        sale.setSaleDate(request.getSaleDate());
        sale.setTotal(0.0);

        double subtotal = processItems(sale, request.getItems());
        double discountedTotal = applyDiscounts(subtotal, sale.getSaleDate().getDayOfWeek());
        sale.setTotal(discountedTotal);

        Sale persisted = saleRepository.save(sale);
        return toResponseFromEntity(persisted);
    }

    @Transactional
    public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found: id=" + id));

        // Restore stock before deleting the sale
        List<SaleItem> items = new ArrayList<>(sale.getItems());
        for (SaleItem item : items) {
            Product product = item.getProduct();
            int availableStock = product.getStock() == null ? 0 : product.getStock();
            product.setStock(availableStock + item.getQuantity());
            productRepository.save(product);
        }

        saleRepository.delete(sale);
    }

    @Transactional(readOnly = true)
    public SaleResponseDTO getTopSale() {
        Sale top = saleRepository.findFirstByOrderByTotalDesc()
                .orElseThrow(() -> new ResourceNotFoundException("No sales available"));
        return toResponseFromEntity(top);
    }

    @Transactional(readOnly = true)
    public SaleByDateResponseDTO getSalesSummaryByDate(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");

        List<Sale> sales = saleRepository.findBySaleDate(date);
        double total = sales.stream().mapToDouble(Sale::getTotal).sum();
        int count = sales.size();

        return SaleByDateResponseDTO.builder()
                .date(date)
                .total(total)
                .cantidadVentas(count)
                .build();
    }

    @Transactional(readOnly = true)
    public List<SaleProductResponseDTO> getProductsBySaleId(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found: id=" + saleId));

        return sale.getItems().stream()
                .map(item -> SaleProductResponseDTO.builder()
                        .productId(item.getProduct().getId())
                        .name(item.getProduct().getName())
                        .brand(item.getProduct().getBrand())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .toList();
    }

    private double applyDiscounts(double subtotal, DayOfWeek day) {
        double total = subtotal;

        for (DiscountStrategy strategy : discountStrategies) {
            if (strategy.isApplicable(subtotal, day)) {
                total = strategy.apply(total);
            }
        }

        return total;
    }

    private double processItems(Sale sale, List<SaleItemRequestDTO> itemRequests) {
        double subtotal = 0.0;

        for (SaleItemRequestDTO itemRequest : itemRequests) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + itemRequest.getProductId()));

            if (Boolean.FALSE.equals(product.getActive())) {
                throw new ResourceNotFoundException("Product is inactive: id=" + product.getId());
            }

            int availableStock = product.getStock() == null ? 0 : product.getStock();
            int requestedQuantity = itemRequest.getQuantity();

            if (availableStock < requestedQuantity) {
                throw new InsufficientStockException(product.getId(), availableStock, requestedQuantity);
            }

            double unitPrice = product.getPrice();
            subtotal += unitPrice * requestedQuantity;

            SaleItem saleItem = SaleItem.builder()
                    .product(product)
                    .quantity(requestedQuantity)
                    .unitPrice(unitPrice)
                    .build();

            sale.addItem(saleItem);

            product.setStock(availableStock - requestedQuantity);
            productRepository.save(product);
        }

        return subtotal;
    }

    private SaleResponseDTO toResponseFromEntity(Sale sale) {
        int totalQuantity = sale.getItems().stream()
                .mapToInt(SaleItem::getQuantity)
                .sum();
        return toResponse(sale, totalQuantity);
    }

    private SaleResponseDTO toResponse(Sale sale, int totalQuantity) {
        return SaleResponseDTO.builder()
                .id(sale.getId())
                .total(sale.getTotal())
                .cantidadProductos(totalQuantity)
                .nombreCliente(sale.getCustomer().getFirstName())
                .apellidoCliente(sale.getCustomer().getLastName())
                .build();
    }
}

