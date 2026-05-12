package org.example.eksamensprojektqrecycle.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// Lombok laver automatisk getters/setters
@Getter
@Setter

// JPA entity for virksomheder
@Entity
public class Business {

    // Primær nøgle i databasen
    @Id

    // Auto increment ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Virksomhedens navn
    private String companyName;

    // Kontaktperson hos virksomheden
    private String contactPerson;

    // Telefonnummer til virksomheden
    private String phoneNumber;

    // Virksomhedens adresse
    private String address;

    // Én virksomhed har én bruger
    // Foreign key gemmes i Business tabellen
    @OneToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    // Én virksomhed kan have mange afhentninger
    // Foreign key gemmes i Collection tabellen
    @OneToMany(mappedBy = "business")
    private List<Collection> collections = new ArrayList<>();

    // Tom constructor
    public Business() {
    }

    // Constructor til oprettelse af virksomhed
    public Business(
            String companyName,
            String contactPerson,
            String phoneNumber,
            String address,
            AppUser appUser
    ) {
        this.companyName = companyName;
        this.contactPerson = contactPerson;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.appUser = appUser;
    }
}