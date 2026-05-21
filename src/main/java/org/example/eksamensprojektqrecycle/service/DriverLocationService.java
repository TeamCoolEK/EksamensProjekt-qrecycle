package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.DriverLocationDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class DriverLocationService {

    private final ConcurrentHashMap<Integer, DriverLocationDTO> locations = new ConcurrentHashMap<>();

    public void updateLocation(DriverLocationDTO dto) {
        locations.put(dto.getDriverId(), dto);
    }

    public DriverLocationDTO getLocation(int driverId) {
        return locations.get(driverId);
    }

    public void removeLocation(int driverId) {
        locations.remove(driverId);
    }
}