package com.customeronboarding.customer.controller;

import com.customeronboarding.customer.dto.CustomerActivityLogDTO;
import com.customeronboarding.customer.service.CustomerActivityService;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerActivityController {

    private final CustomerActivityService activityService;

    public CustomerActivityController(CustomerActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/{id}/activity")
    public List<CustomerActivityLogDTO> getCustomerActivity(@PathVariable Long id) {
        return activityService.getActivityLogsForCustomer(id);
    }

    @PostMapping("/activity")
    public ResponseEntity<Void> logActivity(
            @RequestParam Long customerId,
            @RequestParam String activityType,
            @RequestParam String description) {

        activityService.logActivity(customerId, activityType, description);
        return ResponseEntity.ok().build();
    }
}