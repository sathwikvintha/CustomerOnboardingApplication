package com.customeronboarding.kyc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "KYC_DOCUMENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long docId;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String aadhaarImagePath;

    @Column(nullable = false)
    private String panImagePath;

    @Column(nullable = false)
    private String photoImagePath;

    private Timestamp uploadedAt = Timestamp.from(Instant.now());

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

}
