package org.example.eksamensprojektqrecycle.controller;

import org.example.eksamensprojektqrecycle.model.dto.CreateUserDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.enums.Status;
import org.example.eksamensprojektqrecycle.service.StatisticService;
import org.example.eksamensprojektqrecycle.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
// Base URL til admin endpoints
@RequestMapping("/admin")

// Tillader requests fra frontend
@CrossOrigin("*")
public class AdminController {

    private final UserService userService; // Service bruges til brugerlogik
    private final StatisticService statisticService;

    public AdminController(UserService userService, StatisticService statisticService) { // Constructor injection
        this.userService = userService;
        this.statisticService = statisticService;
    }

    // GET endpoint til hentning af dashboard data
   @GetMapping("/dashboard")
   public ResponseEntity<Integer> getDashboardData() {

        int bagsReadyForPickup = statisticService.getBagsReadyForPickup(); // Henter antal poser klar til afhentning via service
        return ResponseEntity.ok(bagsReadyForPickup); // Returnere antal poser + status 200 (ok)
   }

    // POST endpoint til oprettelse af bruger
    @PostMapping("/users")
    public ResponseEntity<AppUser> createUser(@RequestBody CreateUserDTO dto) {
        AppUser createdUser = userService.createUser(dto); // Opretter bruger via service
        return ResponseEntity.ok(createdUser); // Returnerer bruger + status 200
    }
}