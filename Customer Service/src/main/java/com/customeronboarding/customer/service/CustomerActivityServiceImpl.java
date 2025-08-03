package com.customeronboarding.customer.service;

import com.customeronboarding.customer.dto.CustomerActivityLogDTO;
import com.customeronboarding.customer.entity.CustomerActivityLog;
import com.customeronboarding.customer.repository.CustomerActivityLogRepository;
import com.customeronboarding.customer.service.CustomerActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerActivityServiceImpl implements CustomerActivityService {

    private final CustomerActivityLogRepository repository;

    @Override
    public List<CustomerActivityLogDTO> getActivityLogsForCustomer(Long customerId) {
        return repository.findByCustomerIdOrderByTimestampDesc(customerId)
                .stream()
                .map(log -> CustomerActivityLogDTO.builder()
                        .action(log.getAction())
                        .description(log.getDescription())
                        .timestamp(log.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void logActivity(Long customerId, String action, String description) {
        CustomerActivityLog log = CustomerActivityLog.builder()
                .customerId(customerId)
                .action(action)
                .description(description)
                .timestamp(LocalDateTime.now())
                .build();
        repository.save(log);
    }
}
