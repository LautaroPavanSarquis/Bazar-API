package com.lautaro.bazar_api.service;

import com.lautaro.bazar_api.dto.request.CustomerRequestDTO;
import com.lautaro.bazar_api.dto.response.CustomerResponseDTO;
import com.lautaro.bazar_api.exception.ResourceNotFoundException;
import com.lautaro.bazar_api.model.Customer;
import com.lautaro.bazar_api.repository.CustomerRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO request) {
        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dni(request.getDni())
                .email(request.getEmail())
                .build();

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAll() {
        return customerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: id=" + id));
        return toResponse(customer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: id=" + id));

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setDni(request.getDni());
        customer.setEmail(request.getEmail());

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found: id=" + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerResponseDTO toResponse(Customer customer) {
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .dni(customer.getDni())
                .email(customer.getEmail())
                .build();
    }
}

