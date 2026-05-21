package org.example.eksamensprojektqrecycle.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseRequestDTO {

    private String title;
    private double amount;
    private String receiptBase64;
}