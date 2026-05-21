package org.example.eksamensprojektqrecycle.model.dto;

public class DriverLocationDTO {

    private int driverId;
    private double latitude;
    private double longitude;

    public DriverLocationDTO() {}

    public DriverLocationDTO(int driverId, double latitude, double longitude) {
        this.driverId = driverId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getDriverId() {
        return driverId;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
