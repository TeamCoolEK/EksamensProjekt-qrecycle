package org.example.eksamensprojektqrecycle.controller;

import org.example.eksamensprojektqrecycle.model.dto.CreateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.service.BusinessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// REST controller til virksomhed endpoints
@RestController
// Base URL til business endpoints
@RequestMapping("/business")
// Tillader requests fra frontend
@CrossOrigin(origins = "*")
public class BusinessController {

    // Service bruges til business logik
    private final BusinessService businessService;

    // Constructor injection
    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    // POST endpoint til oprettelse af virksomhed
    @PostMapping("/businesses")
    public ResponseEntity<Business> createBusiness(@RequestBody CreateBusinessDTO dto) {
        // Opretter virksomhed via service
        Business createdBusiness = businessService.createBusiness(dto);
        // Returnerer virksomhed + status 200
        return ResponseEntity.ok(createdBusiness);
    }
}