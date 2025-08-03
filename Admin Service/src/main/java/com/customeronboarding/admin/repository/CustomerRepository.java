package com.customeronboarding.admin.repository;

import com.customeronboarding.admin.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByFullNameContainingIgnoreCase(String fullName);
    List<Customer> findByEmailContainingIgnoreCase(String email);
    long countByKycStatusIsNull();
    Optional<Customer> findByUserId(Long userId);
    Optional<Customer> findByEmail(String email);
    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    long countByKycStatusIsNullAndCreatedAtBetween(LocalDateTime from, LocalDateTime to);

}
