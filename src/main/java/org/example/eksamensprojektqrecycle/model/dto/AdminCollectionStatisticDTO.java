package org.example.eksamensprojektqrecycle.model.dto;

import org.example.eksamensprojektqrecycle.model.enums.Status;

import java.time.LocalDateTime;

public class AdminCollectionStatisticDTO {

    private int id;
    private String businessName;
    private String driverName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int businessBags;
    private int driverBags;
    private int totalBags;
    private Status status;

    public AdminCollectionStatisticDTO(
            int id,
            String businessName,
            String driverName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            int businessBags,
            int driverBags,
            int totalBags,
            Status status
    ) {
        this.id = id;
        this.businessName = businessName;
        this.driverName = driverName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.businessBags = businessBags;
        this.driverBags = driverBags;
        this.totalBags = totalBags;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getDriverName() {
        return driverName;
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

    public int getTotalBags() {
        return totalBags;
    }

    public Status getStatus() {
        return status;
    }
}