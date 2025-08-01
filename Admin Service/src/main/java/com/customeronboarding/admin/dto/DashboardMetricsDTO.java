package com.customeronboarding.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardMetricsDTO {
    private long totalCustomers;
    private long verifiedKycCount;
    private long pendingKycCount;
    private long rejectedKycCount;
}
