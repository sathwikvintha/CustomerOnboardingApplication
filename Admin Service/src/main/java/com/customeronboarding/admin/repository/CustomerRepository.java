package com.customeronboarding.admin.repository;

import com.customeronboarding.admin.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByNameContainingIgnoreCase(String fullName);
    List<Customer> findByEmailContainingIgnoreCase(String email);
}
