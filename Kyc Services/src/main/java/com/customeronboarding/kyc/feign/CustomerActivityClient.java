package com.customeronboarding.kyc.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "customer-service", url = "${CUSTOMER_SERVICE_URL}") // or service discovery
public interface CustomerActivityClient {

    @PostMapping("/api/customers/{id}/activity-log")
    void logActivity(@PathVariable("id") Long customerId,
                     @RequestParam("action") String action,
                     @RequestParam("description") String description);
}