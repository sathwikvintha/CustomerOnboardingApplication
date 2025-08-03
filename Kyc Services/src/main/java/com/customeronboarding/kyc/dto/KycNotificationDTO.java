package com.customeronboarding.kyc.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class KycNotificationDTO {
    private String status;
    private String message; // adminMessage
    private LocalDateTime timestamp;
}
