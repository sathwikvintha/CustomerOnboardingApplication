package com.customeronboarding.kyc.dto;

import java.time.LocalDateTime;

public class KycHistoryDTO {

    private Long kycId;
    private String aadhaarPath;
    private String panPath;
    private String photoPath;
    private LocalDateTime uploadedAt;

    // Getters
    public Long getKycId() {
        return kycId;
    }

    public String getAadhaarPath() {
        return aadhaarPath;
    }

    public String getPanPath() {
        return panPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    // Setters
    public void setKycId(Long kycId) {
        this.kycId = kycId;
    }

    public void setAadhaarPath(String aadhaarPath) {
        this.aadhaarPath = aadhaarPath;
    }

    public void setPanPath(String panPath) {
        this.panPath = panPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
