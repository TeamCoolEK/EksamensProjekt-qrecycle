package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.ExpenseRequestDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Expense;
import org.example.eksamensprojektqrecycle.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public void createExpense(ExpenseRequestDTO dto, AppUser user) {
        validateExpense(dto);
        Expense expense = new Expense();
        expense.setTitle(dto.getTitle());
        expense.setAmount(dto.getAmount());
        expense.setReceiptBase64(dto.getReceiptBase64());
        expense.setDate(LocalDate.now());
        expense.setUser(user);
        expenseRepository.save(expense);
    }

    private void validateExpense(ExpenseRequestDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new RuntimeException("Titel mangler");
        }
        if (dto.getAmount() <= 0) {
            throw new RuntimeException("Beløb skal være større end 0");
        }
        if (dto.getReceiptBase64() == null || dto.getReceiptBase64().isBlank()) {
            throw new RuntimeException("Bilag mangler");
        }
    }
}