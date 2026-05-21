package org.example.eksamensprojektqrecycle.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double amount;

    private String title;

    private String attachment;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String receiptBase64;

    private LocalDate date;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    public Expense() {
    }

    public Expense(double amount, String title, String attachment, String receiptBase64, LocalDate date, AppUser user) {
        this.amount = amount;
        this.title = title;
        this.attachment = attachment;
        this.receiptBase64 = receiptBase64;
        this.date = date;
        this.user = user;
    }
}