package com.customeronboarding.customer.controller;

import com.customeronboarding.customer.config.JwtUtils;
import com.customeronboarding.customer.dto.CustomerChangePasswordRequestDTO;
import com.customeronboarding.customer.dto.CustomerProfileResponseDTO;
import com.customeronboarding.customer.dto.CustomerRequestDTO;
import com.customeronboarding.customer.dto.CustomerResponseDTO;
import com.customeronboarding.customer.entity.AdminUser;
import com.customeronboarding.customer.entity.Customer;
import com.customeronboarding.customer.repository.AdminUserRepository;
import com.customeronboarding.customer.repository.CustomerRepository;
import com.customeronboarding.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer APIs", description = "APIs for customer onboarding")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final AdminUserRepository adminUserRepository;
    private JwtUtils jwtUtils;
    private final com.customeronboarding.customer.service.CustomerActivityService activityService;



    @PostMapping("/register")
    @Operation(summary = "Register a new customer")
    public ResponseEntity<CustomerResponseDTO> registerCustomer(
            @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO response = customerService.registerCustomer(customerRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all customers")
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full customer by ID")
    public ResponseEntity<CustomerResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer by ID")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Long id,
                                                              @Valid @RequestBody CustomerRequestDTO dto) {
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer by ID")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get customer by email")
    public ResponseEntity<CustomerResponseDTO> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam String username,
            @RequestBody CustomerChangePasswordRequestDTO dto) {

        customerService.changePassword(username, dto);
        AdminUser user = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        activityService.logActivity(user.getId(), "PASSWORD_CHANGED", "Customer changed their password");

        return ResponseEntity.ok("Password updated successfully");
    }



    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileResponseDTO> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername(); // email or username
        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerProfileResponseDTO dto = customerService.getCurrentCustomerProfile(customer.getUser().getId());
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateCustomer(@PathVariable Long id) {
        customerService.deactivateCustomer(id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        AdminUser user = adminUserRepository.findById(customer.getCustomerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        activityService.logActivity(user.getId(), "ACCOUNT_DEACTIVATED", "Customer deactivated their account");
        return ResponseEntity.ok("Customer deactivated successfully.");
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<CustomerResponseDTO> getCustomerByUsername(@PathVariable String username) {
        return customerService.getCustomerByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
