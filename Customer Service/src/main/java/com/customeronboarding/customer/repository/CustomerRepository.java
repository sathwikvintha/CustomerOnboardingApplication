package com.customeronboarding.customer.repository;

import com.customeronboarding.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
    boolean existsByUserId(Long userId);
}
