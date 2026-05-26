package org.example.eksamensprojektqrecycle.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.eksamensprojektqrecycle.model.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    //QE-119: Timestamp for hvornår en collection blev oprettet//
    @Column(name ="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //QE-119: Timestamp for hvornår en collection sidst blev opdateret -Opdateres automatisk//
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Mange collections kan tilhøre én virksomhed
    // Foreign key gemmes i Collection tabellen
    @JsonBackReference("business-collection")
    @ManyToOne
    @JoinColumn(name = "business_id")
    private Business business;


    // Tom constructor kræves af JPA
    public Collection() {
    }

    // Constructor til oprettelse af collection
    public Collection(Status status, int businessBags, int driverBags, Business business) {
        this.status = status;
        this.businessBags = businessBags;
        this.driverBags = driverBags;
        this.business = business;
    }

    //QE-119: Sætter createdAt og updatedAt når en collection oprettes første gang//
    @PrePersist
    protected void setCollectionTimestamp() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    //QE-119: Opdaterer updatedAt hver gang en collection ændres//
    @PreUpdate
    protected void updateCollectionTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

}