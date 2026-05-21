package org.example.eksamensprojektqrecycle.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Lombok laver automatisk getters/setters
@Getter
@Setter

// JPA entity for midlertidige stop på en rute
@Entity
public class TempStop {

    // Primær nøgle i databasen
    @Id

    // Auto increment ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Adresse på stoppet
    private String address;

    // Mange temp stops kan tilhøre én rute
    // Foreign key gemmes i TempStop tabellen
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    // Tom constructor kræves af JPA
    public TempStop() {
    }

    // Constructor til oprettelse af temp stop
    public TempStop(String address, Route route) {
        this.address = address;
        this.route = route;
    }
}