package com.customeronboarding.kyc.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "customer-service", url = "${CUSTOMER_SERVICE_URL}")
public interface CustomerActivityClient {

    @PostMapping("/api/customers/activity")
    void logActivity(@RequestParam("customerId") Long customerId,
                     @RequestParam("activityType") String activityType,
                     @RequestParam("description") String description);
}
