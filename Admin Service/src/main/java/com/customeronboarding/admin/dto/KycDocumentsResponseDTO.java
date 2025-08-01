package com.customeronboarding.admin.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KycDocumentsResponseDTO {
    private Long customerId;
    private String aadhaarPath;
    private String panPath;
    private String photoPath;
}
