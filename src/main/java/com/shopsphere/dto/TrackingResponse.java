package com.shopsphere.dto;

import java.time.LocalDateTime;

public class TrackingResponse {

    private String status;
    private LocalDateTime updatedAt;

    public TrackingResponse() {}

    public TrackingResponse(String status, LocalDateTime updatedAt) {
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
