package com.customeronboarding.customer.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerActivityLogDTO {
    private String action;
    private String description;
    private LocalDateTime timestamp;
}
