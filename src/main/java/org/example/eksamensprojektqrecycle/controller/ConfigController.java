package org.example.eksamensprojektqrecycle.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
//Til at importere google api key til frontend
@RestController
@RequestMapping("/config")
public class ConfigController {
    @Value("${google.api.key}")
    private String mapsApiKey;

    @Value("${google.map.id}")
    private String mapId;

    @GetMapping("/maps") //retunere beskyttede google maps variabler til frontenden
    public Map<String, String> getMapsConfig() {
        return Map.of("apiKey", mapsApiKey,
                      "mapId", mapId);
    }
}
