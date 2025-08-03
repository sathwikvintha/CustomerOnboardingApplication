package com.customeronboarding.admin.repository;

import com.customeronboarding.admin.entity.KycStatus;
import com.customeronboarding.admin.entity.KycStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface KycStatusRepository extends JpaRepository<KycStatus, Long> {


    Optional<KycStatus> findByCustomerCustomerId(Long customerId);
    List<KycStatus> findByStatus(KycStatusEnum status);
    long countByStatus(KycStatusEnum status);
    long countByStatusAndVerifiedAtBetween(KycStatusEnum status, LocalDateTime from, LocalDateTime to);
    long countByStatusAndCreatedAtBetween(KycStatusEnum status, LocalDateTime from, LocalDateTime to);

}
