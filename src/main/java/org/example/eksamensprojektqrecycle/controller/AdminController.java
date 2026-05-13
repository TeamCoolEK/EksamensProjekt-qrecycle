package org.example.eksamensprojektqrecycle.controller;

import org.example.eksamensprojektqrecycle.model.dto.CreateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.dto.CreateUserDTO;
import org.example.eksamensprojektqrecycle.model.dto.UpdateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.service.BusinessService;
import org.example.eksamensprojektqrecycle.service.StatisticService;
import org.example.eksamensprojektqrecycle.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// Base URL til admin endpoints
@RequestMapping("/admin")

// Tillader requests fra frontend
@CrossOrigin(
        origins = "http://localhost:63342",
        allowCredentials = "true"
)
public class AdminController {

    private final UserService userService; // Service bruges til brugerlogik
    private final StatisticService statisticService;
    private final BusinessService businessService;

    public AdminController(UserService userService, StatisticService statisticService, BusinessService businessService) { // Constructor injection
        this.userService = userService;
        this.statisticService = statisticService;
        this.businessService = businessService;
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

    @GetMapping("/businesses")
    public ResponseEntity<List<Business>> getAllBusinesses() {
        List<Business> businesses = businessService.getAllBusinesses();

        return ResponseEntity.ok(businesses);
    }

    // POST endpoint til oprettelse af virksomhed
    @PostMapping("/businesses")
    public ResponseEntity<Business> createBusiness(@RequestBody CreateBusinessDTO dto) {
        Business createdBusiness = businessService.createBusiness(dto); // Opretter virksomhed via service
        return ResponseEntity.ok(createdBusiness); // Returnerer virksomhed + status 200
    }

    @PutMapping("/businesses/{id}")
    public ResponseEntity<?> updateBusiness(@PathVariable Integer id, @RequestBody UpdateBusinessDTO dto) {
        try {
            Business updatedBusiness = businessService.updateBusiness(id, dto);
            return ResponseEntity.ok(updatedBusiness);
        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/businesses/{id}")
    public ResponseEntity<?> deleteBusiness(@PathVariable int id) {

        try {
            businessService.deleteBusiness(id);
            return ResponseEntity.ok("Virksomhed slettet");
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}