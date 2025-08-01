package com.customeronboarding.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "KYC_STATUS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class KycStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STATUS_ID")
    private Long id;

    @Enumerated(EnumType.STRING)  // 👈 Convert enum to VARCHAR in DB
    @Column(name = "STATUS", nullable = false, length = 30)
    private KycStatusEnum status;  // 👈 Now using enum instead of String

    @Column(name = "ADMIN_MESSAGE", length = 255)
    private String adminMessage;

    @Column(name = "VERIFIED_BY")
    private String verifiedBy;

    @Column(name = "VERIFIED_AT")
    private LocalDateTime verifiedAt;

    @OneToOne
    @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
