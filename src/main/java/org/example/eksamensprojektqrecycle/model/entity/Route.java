package org.example.eksamensprojektqrecycle.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.eksamensprojektqrecycle.model.enums.Status;

import java.util.ArrayList;
import java.util.List;

// Lombok laver automatisk getters/setters
@Getter
@Setter

// JPA entity for ruter
@Entity
public class Route {

    // Primær nøgle i databasen
    @Id

    // Auto increment ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Status på ruten
    // Gemmes som tekst i databasen
    @Enumerated(EnumType.STRING)
    private Status status;

    // Én rute kan have mange collections
    // Foreign key gemmes i Collection tabellen
    @OneToMany(mappedBy = "route")
    private List<Collection> collections = new ArrayList<>();

    // Én rute kan have mange midlertidige stop
    // Foreign key gemmes i TempStop tabellen
    @OneToMany(mappedBy = "route")
    private List<TempStop> tempStops = new ArrayList<>();

    // Tom constructor
    public Route() {
    }

    // Constructor til oprettelse af route
    public Route(Status status) {
        this.status = status;
    }
}