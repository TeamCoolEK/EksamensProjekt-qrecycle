package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.DriverLocationDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class DriverLocationService {

    private final ConcurrentHashMap<String, DriverLocationDTO> locations = new ConcurrentHashMap<>();

    public void updateLocation(String username, DriverLocationDTO dto) {
        locations.put(username, dto);
    }

    public DriverLocationDTO getLocation(String username) {
        return locations.get(username);
    }

    public void removeLocation(String username) {
        locations.remove(username);
    }
}