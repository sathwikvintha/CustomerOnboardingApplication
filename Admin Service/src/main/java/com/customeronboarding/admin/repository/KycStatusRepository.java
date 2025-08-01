package com.customeronboarding.admin.repository;

import com.customeronboarding.admin.entity.KycStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KycStatusRepository extends JpaRepository<KycStatus, Long> {


    Optional<KycStatus> findByCustomerCustomerId(Long customerId);
    List<KycStatus> findByStatus(String status);
    Page<KycStatus> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);
}
