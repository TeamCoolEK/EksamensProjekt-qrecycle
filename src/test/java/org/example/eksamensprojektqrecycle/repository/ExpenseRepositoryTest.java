package org.example.eksamensprojektqrecycle.repository;


import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Expense;
import org.example.eksamensprojektqrecycle.model.enums.Role;
import org.example.eksamensprojektqrecycle.repository.ExpenseRepository;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_shouldSaveExpenseInDatabase() {
        AppUser user = new AppUser();
        user.setUsername("driver1");
        user.setPassword("Password1!");
        user.setRole(Role.DRIVER);

        AppUser savedUser = userRepository.save(user);

        Expense expense = new Expense();
        expense.setTitle("Benzin");
        expense.setAmount(150);
        expense.setReceiptBase64("data:image/png;base64,test");
        expense.setDate(LocalDate.now());
        expense.setUser(savedUser);

        Expense savedExpense = expenseRepository.save(expense);

        assertNotNull(savedExpense.getId());
        assertEquals("Benzin", savedExpense.getTitle());
        assertEquals(150, savedExpense.getAmount());
        assertEquals("data:image/png;base64,test", savedExpense.getReceiptBase64());
        assertNotNull(savedExpense.getDate());
        assertEquals(savedUser, savedExpense.getUser());
    }
}