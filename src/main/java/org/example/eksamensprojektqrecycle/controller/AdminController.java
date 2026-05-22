package org.example.eksamensprojektqrecycle.controller;

import org.example.eksamensprojektqrecycle.model.dto.*;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.model.entity.Expense;
import org.example.eksamensprojektqrecycle.service.*;
import org.example.eksamensprojektqrecycle.model.dto.AdminCollectionStatisticDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// Base URL til admin endpoints
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService; // Service bruges til brugerlogik
    private final StatisticService statisticService;
    private final BusinessService businessService;
    private final PickupService pickupService;
    private final ExpenseService expenseService;

    public AdminController(UserService userService, StatisticService statisticService, BusinessService businessService, PickupService pickupService, ExpenseService expenseService) { // Constructor injection
        this.userService = userService;
        this.statisticService = statisticService;
        this.businessService = businessService;
        this.pickupService = pickupService;
        this.expenseService = expenseService;
    }

    // GET endpoint til hentning af dashboard data
   @GetMapping("/dashboard")
   public ResponseEntity<Integer> getDashboardData() {

        int bagsReadyForPickup = statisticService.getBagsReadyForPickup(); // Henter antal poser klar til afhentning via service
        return ResponseEntity.ok(bagsReadyForPickup); // Returnere antal poser + status 200 (ok)
   }

   //QE-321: GET endpoint til hentning af alle brugere
   @GetMapping("/users")
   public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
       List<UserResponseDTO> dtos = userService.getAllUsers();
       return ResponseEntity.ok(dtos);
   }

    // POST - opret bruger
    @PostMapping("/users")
    public ResponseEntity<AppUser> createUser(@RequestBody CreateUserDTO dto) {
        AppUser createdUser = userService.createUser(dto); // Opretter bruger via service
        return ResponseEntity.ok(createdUser); // Returnerer bruger + status 200
    }

    //Slet bruger
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("Bruger slettet");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessResponseDTO>> getAllBusinesses() {
        return ResponseEntity.ok(businessService.getAllBusinesses());
    }

    // POST endpoint til oprettelse af virksomhed
    @PostMapping("/businesses")
    public ResponseEntity<Business> createBusiness(@RequestBody CreateBusinessDTO dto) {
        Business createdBusiness = businessService.createBusiness(dto); // Opretter virksomhed via service
        return ResponseEntity.ok(createdBusiness); // Returnerer virksomhed + status 200
    }

    @PutMapping("/businesses/{id}")
    public ResponseEntity<?> updateBusiness(
            @PathVariable Integer id,
            @RequestBody UpdateBusinessDTO dto
    ) {
        try {
            BusinessResponseDTO updatedBusiness = businessService.updateBusiness(id, dto);
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

    @GetMapping("/collections")
    public ResponseEntity<List<CollectionResponseDTO>> getAllCollections() {
        return ResponseEntity.ok(pickupService.getAllCollectionsForAdmin());
    }

    @GetMapping("/statistics/collections")
    public ResponseEntity<List<AdminCollectionStatisticDTO>> getCollectionStatistics(){
        return ResponseEntity.ok(statisticService.getCollectionStatisticsForAdmin());
    }

    //henter alle expenses
    @GetMapping("/business/expenses")
    public ResponseEntity<List<GetExpensesDTO>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }
}