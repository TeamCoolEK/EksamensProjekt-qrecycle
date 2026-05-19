package org.example.eksamensprojektqrecycle.model.dto;

import org.example.eksamensprojektqrecycle.model.enums.Status;

import java.time.LocalDateTime;

public class CollectionResponseDTO {

    private int id;
    private String businessName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int businessBags;
    private int driverBags;
    private Status status;

    public CollectionResponseDTO(
            int id,
            String businessName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            int businessBags,
            int driverBags,
            Status status
    ) {
        this.id = id;
        this.businessName = businessName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.businessBags = businessBags;
        this.driverBags = driverBags;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public int getBusinessBags() {
        return businessBags;
    }

    public int getDriverBags() {
        return driverBags;
    }

    public Status getStatus() {
        return status;
    }
}