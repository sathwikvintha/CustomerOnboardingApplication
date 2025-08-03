package com.customeronboarding.customer.service;

import com.customeronboarding.customer.dto.CustomerChangePasswordRequestDTO;
import com.customeronboarding.customer.dto.CustomerProfileResponseDTO;
import com.customeronboarding.customer.dto.CustomerRequestDTO;
import com.customeronboarding.customer.dto.CustomerResponseDTO;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    CustomerResponseDTO registerCustomer(CustomerRequestDTO customerRequestDTO);
    List<CustomerResponseDTO> getAllCustomers();
    CustomerResponseDTO getCustomerById(Long id);
    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto);
    void deleteCustomer(Long id);
    CustomerResponseDTO getCustomerByEmail(String email);
//    void changePassword(Long userId, CustomerChangePasswordRequestDTO dto);
    void changePassword(String token, CustomerChangePasswordRequestDTO request);
    CustomerProfileResponseDTO getCurrentCustomerProfile(Long userId);
    CustomerResponseDTO getCustomerProfile(String token);
    void deactivateCustomer(Long customerId);
    Optional<CustomerResponseDTO> getCustomerByUsername(String username);
}
