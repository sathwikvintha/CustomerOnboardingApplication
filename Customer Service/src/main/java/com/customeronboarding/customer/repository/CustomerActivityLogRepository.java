package com.customeronboarding.customer.repository;

import com.customeronboarding.customer.entity.CustomerActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerActivityLogRepository extends JpaRepository<CustomerActivityLog, Long> {
    List<CustomerActivityLog> findByCustomerIdOrderByTimestampDesc(Long customerId);
}
