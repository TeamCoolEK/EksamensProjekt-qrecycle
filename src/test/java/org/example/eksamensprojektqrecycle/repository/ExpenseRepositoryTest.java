package org.example.eksamensprojektqrecycle.repository;

import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Expense;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


// Starter kun JPA/database delen af Spring
@DataJpaTest
class ExpenseRepositoryTest {

    // Injicerer repository som testes
    @Autowired
    private ExpenseRepository expenseRepository;


    // Tester at expense kan gemmes i databasen
    @Test
    void save_shouldSaveExpenseInDatabase() {

        // Opretter test bruger
        AppUser user = new AppUser();

        // Opretter test expense
        Expense expense = new Expense();

        expense.setTitle("Benzin");
        expense.setAmount(150);
        expense.setReceiptBase64("data:image/png;base64,test");

        // Sætter dato automatisk
        expense.setDate(LocalDate.now());

        // Knytter expense til bruger
        expense.setUser(user);

        // Gemmer expense i test database
        Expense savedExpense =
                expenseRepository.save(expense);

        // Tjekker at expense fik et ID
        assertNotNull(savedExpense.getId());

        // Tjekker at titel blev gemt korrekt
        assertEquals("Benzin", savedExpense.getTitle());

        // Tjekker at amount blev gemt korrekt
        assertEquals(150, savedExpense.getAmount());

        // Tjekker at Base64 billede blev gemt
        assertEquals("data:image/png;base64,test", savedExpense.getReceiptBase64());

        // Tjekker at dato blev gemt
        assertNotNull(savedExpense.getDate());

        // Tjekker at bruger relation blev gemt
        assertEquals(user, savedExpense.getUser());
    }
}