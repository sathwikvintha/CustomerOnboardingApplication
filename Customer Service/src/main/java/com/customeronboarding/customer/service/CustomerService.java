package com.customeronboarding.customer.service;

import com.customeronboarding.customer.dto.CustomerRequestDTO;
import com.customeronboarding.customer.dto.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {
    CustomerResponseDTO registerCustomer(CustomerRequestDTO customerRequestDTO);
    List<CustomerResponseDTO> getAllCustomers();
    CustomerResponseDTO getCustomerById(Long id);
    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto);
    void deleteCustomer(Long id);
    CustomerResponseDTO getCustomerByEmail(String email); // optional

}
