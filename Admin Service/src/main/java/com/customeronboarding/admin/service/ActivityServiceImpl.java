package com.customeronboarding.admin.service;

import com.customeronboarding.admin.entity.ActivityLog;
import com.customeronboarding.admin.repository.ActivityLogRepository;
import com.customeronboarding.admin.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Override
    public void logActivity(Long userId, String action, String description) {
        ActivityLog log = ActivityLog.builder()
                .userId(userId)
                .action(action)
                .description(description)
                .timestamp(LocalDateTime.now())
                .build();
        activityLogRepository.save(log);
    }
}
