package org.example.eksamensprojektqrecycle.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DriverLocationDTO {

    private double latitude;
    private double longitude;

    public DriverLocationDTO() {}

    public DriverLocationDTO(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;

    }
}
