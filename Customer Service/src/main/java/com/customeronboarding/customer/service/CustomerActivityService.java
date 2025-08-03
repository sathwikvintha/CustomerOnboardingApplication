package com.customeronboarding.customer.service;

import com.customeronboarding.customer.dto.CustomerActivityLogDTO;

import java.util.List;

public interface CustomerActivityService {
    List<CustomerActivityLogDTO> getActivityLogsForCustomer(Long customerId);
    void logActivity(Long customerId, String action, String description);
}
