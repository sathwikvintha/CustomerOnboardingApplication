package com.customeronboarding.customer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CUSTOMER_ACTIVITY_LOG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private String action; // e.g., "REGISTERED", "LOGGED_IN", "KYC_SUBMITTED"

    private String description;

    private LocalDateTime timestamp;
}
