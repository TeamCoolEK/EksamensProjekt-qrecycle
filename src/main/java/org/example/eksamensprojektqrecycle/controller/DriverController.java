package org.example.eksamensprojektqrecycle.controller;

import jakarta.servlet.http.HttpSession;
import org.example.eksamensprojektqrecycle.model.dto.CollectionRequestDTO;
import org.example.eksamensprojektqrecycle.model.dto.ExpenseRequestDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.service.ExpenseService;
import org.example.eksamensprojektqrecycle.service.PickupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/driver")
@CrossOrigin(origins = "http://localhost:63342",
        allowCredentials = "true"
)
public class DriverController {

    private final ExpenseService expenseService;
    private final PickupService pickupService;

    public DriverController(ExpenseService expenseService, PickupService pickupService) {
        this.expenseService = expenseService;
        this.pickupService = pickupService;
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
            @RequestBody ExpenseRequestDTO dto, HttpSession session) {

        AppUser loggedInUser =
                (AppUser) session.getAttribute("user");

        if (loggedInUser == null) {
            return ResponseEntity
                    .status(401)
                    .body("Ikke logget ind");
        }

        expenseService.createExpense(dto, loggedInUser);

        return ResponseEntity.ok("Udgift gemt");
    }

}