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
    @Value("${GOOGLE_API_KEY}")
    private String mapsApiKey;

    @GetMapping("/maps")
    public Map<String, String> getMapsConfig() {
        return Map.of("apiKey", mapsApiKey);
    }
}
