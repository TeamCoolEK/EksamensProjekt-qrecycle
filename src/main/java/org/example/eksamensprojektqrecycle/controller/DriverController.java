package org.example.eksamensprojektqrecycle.controller;

import jakarta.servlet.http.HttpSession;
import org.example.eksamensprojektqrecycle.model.dto.ExpenseRequestDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Expense;
import org.example.eksamensprojektqrecycle.repository.ExpenseRepository;
import org.example.eksamensprojektqrecycle.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driver")
@CrossOrigin(origins = "http://localhost:63342",
        allowCredentials = "true"
)
public class DriverController {

    private final ExpenseService expenseService;
    private final ExpenseRepository expenseRepository;

    public DriverController(ExpenseService expenseService, ExpenseRepository expenseRepository) {
        this.expenseService = expenseService;
        this.expenseRepository = expenseRepository;
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