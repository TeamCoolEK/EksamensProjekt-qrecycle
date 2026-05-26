package org.example.eksamensprojektqrecycle.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.eksamensprojektqrecycle.model.enums.Status;

import java.time.LocalDateTime;

@Getter
@Setter
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

}