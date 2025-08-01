package com.customeronboarding.customer.service;

import com.customeronboarding.customer.dto.CustomerRequestDTO;
import com.customeronboarding.customer.dto.CustomerResponseDTO;
import com.customeronboarding.customer.entity.Customer;
import com.customeronboarding.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponseDTO registerCustomer(CustomerRequestDTO dto) {
        if (customerRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Email already exists");

        if (customerRepository.existsByUserId(dto.getUserId()))
            throw new RuntimeException("User ID already mapped");

        Customer customer = Customer.builder()
                .userId(dto.getUserId())
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .dob(LocalDate.parse(dto.getDob()))
                .address(dto.getAddress())
                .panNumber(dto.getPanNumber())
                .aadhaarNumber(dto.getAadhaarNumber())
                .build();

        Customer saved = customerRepository.save(customer);
        return mapToDTO(saved);
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return mapToDTO(c);
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setDob(LocalDate.parse(dto.getDob()));
        customer.setAddress(dto.getAddress());
        customer.setPanNumber(dto.getPanNumber());
        customer.setAadhaarNumber(dto.getAadhaarNumber());

        Customer updated = customerRepository.save(customer);
        return mapToDTO(updated);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id))
            throw new RuntimeException("Customer not found");
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerResponseDTO getCustomerByEmail(String email) {
        Customer c = customerRepository.findAll().stream()
                .filter(cust -> cust.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Customer not found by email"));

        return mapToDTO(c);
    }

    // Utility mapper method
    private CustomerResponseDTO mapToDTO(Customer c) {
        return CustomerResponseDTO.builder()
                .customerId(c.getCustomerId())
                .userId(c.getUserId())
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .dob(c.getDob().toString())
                .address(c.getAddress())
                .panNumber(c.getPanNumber())
                .aadhaarNumber(c.getAadhaarNumber())
                .createdAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null)
                .build();
    }
}
