package org.example.eksamensprojektqrecycle.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.eksamensprojektqrecycle.model.Status;

import java.time.LocalDate;

// Lombok laver automatisk getters/setters
@Getter
@Setter

// JPA entity for afhentninger
@Entity
public class Collection {

    // Primær nøgle i databasen
    @Id

    // Auto increment ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Status på afhentningen
    // Gemmes som tekst i databasen
    @Enumerated(EnumType.STRING)
    private Status status;

    // Antal poser registreret af virksomheden
    private int businessBags;

    // Antal poser registreret af chaufføren
    private int driverBags;

    // Dato for afhentningen
    private LocalDate date;

    // Mange collections kan tilhøre én virksomhed
    // Foreign key gemmes i Collection tabellen
    @ManyToOne
    @JoinColumn(name = "business_id")
    private Business business;

    // Mange collections kan tilhøre én rute
    // Foreign key gemmes i Collection tabellen
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    // Tom constructor kræves af JPA
    public Collection() {
    }

    // Constructor til oprettelse af collection
    public Collection(Status status, int businessBags, int driverBags, LocalDate date, Business business, Route route) {
        this.status = status;
        this.businessBags = businessBags;
        this.driverBags = driverBags;
        this.date = date;
        this.business = business;
        this.route = route;
    }
}