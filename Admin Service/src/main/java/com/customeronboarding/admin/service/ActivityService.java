package com.customeronboarding.admin.service;

public interface ActivityService {
    void logActivity(Long userId, String action, String description);
}
