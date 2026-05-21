package org.example.eksamensprojektqrecycle.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class GetExpensesDTO {
    private double amount;
    private LocalDate date;
    private String receiptBase64;
    private String title;
    private int userId;
}
