package org.example.eksamensprojektqrecycle.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Lombok laver automatisk getters/setters
@Getter
@Setter

// JPA entity for udgifter
@Entity
public class Expense {

    // Primær nøgle i databasen
    @Id

    // Auto increment ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Beløb på udgiften
    private int value;

    // Titel på udgiften
    private String title;

    // Filnavn eller sti til kvittering/billede
    private String attachment;

    // Mange expenses kan tilhøre én bruger
    // Foreign key gemmes i Expense tabellen
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    // Tom constructor
    public Expense() {
    }

    // Constructor til oprettelse af expense
    public Expense(int value, String title, String attachment, AppUser user) {
        this.value = value;
        this.title = title;
        this.attachment = attachment;
        this.user = user;
    }
}