package org.example.eksamensprojektqrecycle.controller;

import org.example.eksamensprojektqrecycle.model.dto.CreateUserDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
// Base URL til admin endpoints
@RequestMapping("/admin")

// Tillader requests fra frontend
@CrossOrigin(
        origins = "http://localhost:63342",
        allowCredentials = "true"
)
public class AdminController {
    // Service bruges til brugerlogik
    private final UserService userService;
    // Constructor injection
    public AdminController(UserService userService) {
        this.userService = userService;
    }
    // POST endpoint til oprettelse af bruger
    @PostMapping("/users")
    public ResponseEntity<AppUser> createUser(@RequestBody CreateUserDTO dto) {
        // Opretter bruger via service
        AppUser createdUser = userService.createUser(dto);
        // Returnerer bruger + status 200
        return ResponseEntity.ok(createdUser);
    }
}