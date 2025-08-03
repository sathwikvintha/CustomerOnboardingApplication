package com.customeronboarding.customer.service;

import com.customeronboarding.customer.config.JwtUtils;
import com.customeronboarding.customer.dto.CustomerChangePasswordRequestDTO;
import com.customeronboarding.customer.dto.CustomerProfileResponseDTO;
import com.customeronboarding.customer.dto.CustomerRequestDTO;
import com.customeronboarding.customer.dto.CustomerResponseDTO;
import com.customeronboarding.customer.entity.AdminUser;
import com.customeronboarding.customer.entity.Customer;
import com.customeronboarding.customer.exception.ResourceNotFoundException;
import com.customeronboarding.customer.mapper.CustomerMapper;
import com.customeronboarding.customer.repository.AdminUserRepository;
import com.customeronboarding.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final JwtUtils jwtUtils;
    private final CustomerMapper customerMapper;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final com.customeronboarding.customer.service.CustomerActivityService activityService;



    @Override
    public CustomerResponseDTO registerCustomer(CustomerRequestDTO dto) {
        if (customerRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Email already exists");

        if (customerRepository.existsByUserId(dto.getUserId()))
            throw new RuntimeException("User ID already mapped");

        AdminUser user = adminUserRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        Customer customer = Customer.builder()
                .user(user)
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
                .userId(c.getUser().getId())
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .dob(LocalDate.parse(c.getDob().toString()))
                .address(c.getAddress())
                .panNumber(c.getPanNumber())
                .aadhaarNumber(c.getAadhaarNumber())
                .isActive(c.getIsActive().name())
                .createdAt(c.getCreatedAt() != null ? LocalDateTime.parse(c.getCreatedAt().toString()) : null)
                .build();
    }

    @Override
    public void changePassword(String username, CustomerChangePasswordRequestDTO dto) {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), adminUser.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }

        adminUser.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        adminUserRepository.save(adminUser);

        // Log activity for customer
        Customer customer = customerRepository.findByUserId(adminUser.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        activityService.logActivity(adminUser.getId(), "PASSWORD_CHANGED", "Customer changed their password");
    }




    @Override
    public CustomerProfileResponseDTO getCurrentCustomerProfile(Long userId) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found for user ID: " + userId));

        CustomerProfileResponseDTO dto = new CustomerProfileResponseDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setUserId(customer.getUser().getId());
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setDob(customer.getDob());
        dto.setAddress(customer.getAddress());
        dto.setPanNumber(customer.getPanNumber());
        dto.setAadhaarNumber(customer.getAadhaarNumber());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setCreatedBy(customer.getCreatedBy());
        dto.setUpdatedBy(customer.getUpdatedBy());
        dto.setUpdatedAt(customer.getUpdatedAt());

        return dto;
    }

    @Override
    public CustomerResponseDTO getCustomerProfile(String token) {
        String username = jwtUtils.extractUsername(token);

        AdminUser user = adminUserRepository.findByUsername(username)
                .orElseThrow(new Supplier<RuntimeException>() {
                    @Override
                    public RuntimeException get() {
                        return new RuntimeException("User not found");
                    }
                });


        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(new Supplier<RuntimeException>() {
                    @Override
                    public RuntimeException get() {
                        return new RuntimeException("Customer not found");
                    }
                });
        return mapToDto(customer);
    }

    private CustomerResponseDTO mapToDto(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setUserId(customer.getUser().getId());
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setDob(LocalDate.parse(String.valueOf(customer.getDob())));
        dto.setAddress(customer.getAddress());
        dto.setPanNumber(customer.getPanNumber());
        dto.setAadhaarNumber(customer.getAadhaarNumber());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setCreatedBy(customer.getCreatedBy());
        dto.setUpdatedBy(customer.getUpdatedBy());
        dto.setUpdatedAt(customer.getUpdatedAt());
        return dto;
    }

    @Override
    public void deactivateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        customer.setIsActive(Customer.Status.INACTIVE);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Override
    public Optional<CustomerResponseDTO> getCustomerByUsername(String username) {
        return customerRepository.findByUsername(username)
                .map(customerMapper::toResponseDto);
    }
}
