package com.customeronboarding.customer.mapper;

import com.customeronboarding.customer.dto.CustomerResponseDTO;
import com.customeronboarding.customer.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponseDTO toResponseDto(Customer customer) {
        if (customer == null) return null;

        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setUserId(customer.getUser().getId());
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getEmail());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());
        dto.setIsActive(customer.getIsActive().name()); // Convert enum to string
        return dto;
    }
}
