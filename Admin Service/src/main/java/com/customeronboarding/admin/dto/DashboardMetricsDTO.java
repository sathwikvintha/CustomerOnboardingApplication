package com.customeronboarding.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder // ✅ This enables the .builder() method
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsDTO {
    private long totalCustomers;
    private long kycVerified;
    private long kycPending;
    private long kycRejected;
    private long kycNotSubmitted;
}
