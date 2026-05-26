package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.ExpenseRequestDTO;
import org.example.eksamensprojektqrecycle.model.dto.GetExpensesDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Expense;
import org.example.eksamensprojektqrecycle.repository.ExpenseRepository;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            UserRepository userRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public void createExpense(ExpenseRequestDTO dto, String username) {

        validateExpense(dto);

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Bruger blev ikke fundet"));

        Expense expense = new Expense();

        expense.setTitle(dto.getTitle());
        expense.setAmount(dto.getAmount());
        expense.setReceiptBase64(dto.getReceiptBase64());
        expense.setDate(LocalDate.now());
        expense.setUser(user);

        expenseRepository.save(expense);

        System.out.println(dto.getReceiptBase64().substring(0, 50));
        System.out.println("Length: " + dto.getReceiptBase64().length());
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

    //Henter alle expenses fra db
    public List<GetExpensesDTO> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();

        return expenses.stream()
                .map(expense -> {
                    GetExpensesDTO dto = new GetExpensesDTO();
                    dto.setAmount(expense.getAmount());
                    dto.setDate(expense.getDate());
                    dto.setReceiptBase64(expense.getReceiptBase64());
                    dto.setTitle(expense.getTitle());
                    dto.setUserId(expense.getUser().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}