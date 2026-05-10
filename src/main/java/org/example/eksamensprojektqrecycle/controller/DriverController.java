package org.example.eksamensprojektqrecycle.controller;

import jakarta.servlet.http.HttpSession;
import org.example.eksamensprojektqrecycle.model.dto.ExpenseRequestDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
@CrossOrigin(origins = "http://localhost:63342",
        allowCredentials = "true"
)
public class DriverController {

    private final ExpenseService expenseService;

    public DriverController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/expenses")
    public ResponseEntity<?> createExpense(
            @RequestBody ExpenseRequestDTO dto,
            HttpSession session
    ) {

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