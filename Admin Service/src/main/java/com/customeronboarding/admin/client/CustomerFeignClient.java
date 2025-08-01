package com.customeronboarding.admin.client;

import com.customeronboarding.admin.dto.CustomerResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "customer-service", url = "http://localhost:8081/api/customers")
public interface CustomerFeignClient {

    @GetMapping
    List<CustomerResponseDTO> getAllCustomers();

    @DeleteMapping("/{customerId}")
    String deleteCustomer(@PathVariable Long customerId);
}
