package com.customeronboarding.kyc.repository;

import com.customeronboarding.kyc.entity.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycStatusRepository extends JpaRepository<KycStatus, Long> {
    Optional<KycStatus> findByCustomerId(Long customerId);
}
