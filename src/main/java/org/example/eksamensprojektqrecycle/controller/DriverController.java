package org.example.eksamensprojektqrecycle.controller;

import org.example.eksamensprojektqrecycle.model.dto.CollectionRequestDTO;
import org.example.eksamensprojektqrecycle.model.dto.DriverLocationDTO;
import org.example.eksamensprojektqrecycle.model.dto.ExpenseRequestDTO;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.service.DriverLocationService;
import org.example.eksamensprojektqrecycle.service.ExpenseService;
import org.example.eksamensprojektqrecycle.service.PickupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driver")
public class DriverController {

    private final ExpenseService expenseService;
    private final PickupService pickupService;
    private final DriverLocationService driverLocationService;

    public DriverController(ExpenseService expenseService, PickupService pickupService, DriverLocationService driverLocationService) {
        this.expenseService = expenseService;
        this.pickupService = pickupService;
        this.driverLocationService = driverLocationService;
    }

    // Henter aktive afhentninger
    @GetMapping("/collections/active")
    public ResponseEntity<?> getActiveCollections() {

        List<Collection> collections = pickupService.getActiveCollections();

        List<CollectionRequestDTO> response = collections.stream()
                // For hver Collection c i listen= lav den om til en DTO
                // fordi vi ikke vil sende hele Collection objektet til frontend
                .map(c -> {

                    CollectionRequestDTO dto = new CollectionRequestDTO();
                    dto.setId(c.getId());
                    dto.setBusinessName(c.getBusiness().getCompanyName());
                    dto.setAddress(c.getBusiness().getAddress());
                    dto.setStatus(c.getStatus());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    // Markerer afhentning som afsluttet
    @PutMapping("/collections/{id}/complete")
    public ResponseEntity<?> completeCollection(
            @PathVariable int id,
            @RequestBody CollectionRequestDTO dto) {

        // Kalder PickupService med id og antal poser
        // Service finder Collection med det id
        // Sætter status til AFHENTET og gemmer driverBags i databasen
        pickupService.completeCollection(id, dto.getDriverBags());
        return ResponseEntity.ok("Afhentning afsluttet");
    }


    @PostMapping("/expenses")
    public ResponseEntity<?> createExpense(
            @RequestBody ExpenseRequestDTO dto,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Ikke logget ind");
        }

        expenseService.createExpense(dto, authentication.getName());

        return ResponseEntity.ok("Udgift gemt");
    }

    // Opdaterer chaufføren position i hukommelsen (ConcurrentHashMap)
    @PostMapping("/location")
    public ResponseEntity<?> updateLocation(
            @RequestBody DriverLocationDTO dto,
            Authentication authentication) {

        // SecurityConfig tillader kun DRIVER og ADMIN på /driver/**
        // Ingen if-sætninger nødvendige af ovenstående grund.
        String username = authentication.getName();
        driverLocationService.updateLocation(username, dto);
        return ResponseEntity.ok("Position opdateret");
    }


    // Henter chaufførens seneste position fra hukommelsen
    @GetMapping("/location")
    public ResponseEntity<?> getLocation(Authentication authentication) {
        String username = authentication.getName();
        DriverLocationDTO location = driverLocationService.getLocation(username);

        //Hvis positionen findes returneres hele DriverLocationDTO som JSON
        //Hvis ikke returneres 404 med fejlbesked
        if (location == null) {
            return ResponseEntity.status(404).body("Ingen aktiv position fundet — genstart venligst sporing");
        }

        return ResponseEntity.ok(location);
    }

    /*Fjerner chaufføren position fra hukommelsen ved ruteafslutning,
    altså når listen med collections er alle markeret som afhentet. */
    @DeleteMapping("/location")
    public ResponseEntity<?> removeLocation(Authentication authentication) {
        System.out.println("removeLocation kaldt");
        String username = authentication.getName();
        driverLocationService.removeLocation(username);
        return ResponseEntity.ok("Position fjernet");
    }


    // Fanger RuntimeException og returnerer 500 med fejlbesked
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(500).body(e.getMessage());
    }
}
