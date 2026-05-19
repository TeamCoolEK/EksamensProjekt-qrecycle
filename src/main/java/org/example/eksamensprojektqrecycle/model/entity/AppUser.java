package org.example.eksamensprojektqrecycle.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.eksamensprojektqrecycle.model.enums.Role;

import java.util.ArrayList;
import java.util.List;

// JPA entity for systemets brugere
@Entity

// Lombok laver automatisk getters/setters
@Getter
@Setter

public class AppUser {

    // Primær nøgle i databasen
    @Id

    // Auto increment ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Brugernavn til login
    private String username;

    // Password til login
    private String password;

    // Brugerens rolle (ADMIN, DRIVER, BUSINESS osv.)
    @Enumerated(EnumType.STRING)
    private Role role;

    // Inverse side af OneToOne relation til Business
    // Foreign key gemmes i Business tabellen
    @JsonIgnore
    @OneToOne(mappedBy = "appUser")
    private Business business;

    // Én bruger kan have mange expenses
    // Foreign key gemmes i Expense tabellen
    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Expense> expenses = new ArrayList<>();

    // Tom constructor
    public AppUser() {
    }

    // Constructor til oprettelse af brugere
    public AppUser(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}