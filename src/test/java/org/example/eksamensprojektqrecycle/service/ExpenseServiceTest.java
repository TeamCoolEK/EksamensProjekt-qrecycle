package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.ExpenseRequestDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Expense;
import org.example.eksamensprojektqrecycle.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseServiceTest {

    // Mock version af repository
    private final ExpenseRepository expenseRepository =
            mock(ExpenseRepository.class);
    // Service som testes
    private final ExpenseService expenseService =
            new ExpenseService(expenseRepository);


    // Tester at expense gemmes korrekt når data er valid
    @Test
    void createExpense_shouldSaveExpense_whenDataIsValid() {

        // Opretter test DTO
        ExpenseRequestDTO dto = new ExpenseRequestDTO();
        dto.setTitle("Benzin");
        dto.setAmount(150);
        dto.setReceiptBase64("data:image/png;base64,test");

        // Opretter test bruger
        AppUser user = new AppUser();

        // Kalder service metode
        expenseService.createExpense(dto, user);

        // Fanger expense objektet som sendes til repository
        ArgumentCaptor<Expense> captor =
                ArgumentCaptor.forClass(Expense.class);

        // Verificerer at save() blev kaldt
        verify(expenseRepository).save(captor.capture());

        // Henter gemt expense objekt
        Expense savedExpense = captor.getValue();

        // Tjekker at data blev gemt korrekt
        assertEquals("Benzin", savedExpense.getTitle());
        assertEquals(150, savedExpense.getAmount());
        assertEquals("data:image/png;base64,test", savedExpense.getReceiptBase64());

        assertEquals(user, savedExpense.getUser());

        // Tjekker at dato blev sat automatisk
        assertNotNull(savedExpense.getDate());
    }


    // Tester at exception kastes hvis titel er tom
    @Test
    void createExpense_shouldThrowException_whenTitleIsEmpty() {

        ExpenseRequestDTO dto = new ExpenseRequestDTO();

        dto.setTitle("");
        dto.setAmount(150);
        dto.setReceiptBase64("data:image/png;base64,test");

        AppUser user = new AppUser();

        // Forventer RuntimeException
        assertThrows(RuntimeException.class, () -> {

            expenseService.createExpense(dto, user);
        });

        // Verificerer at save() aldrig bliver kaldt
        verify(expenseRepository, never()).save(any());
    }


    // Tester at exception kastes hvis amount er 0
    @Test
    void createExpense_shouldThrowException_whenAmountIsZero() {

        ExpenseRequestDTO dto = new ExpenseRequestDTO();

        dto.setTitle("Benzin");
        dto.setAmount(0);
        dto.setReceiptBase64("data:image/png;base64,test");

        AppUser user = new AppUser();

        // Forventer RuntimeException
        assertThrows(RuntimeException.class, () -> {

            expenseService.createExpense(dto, user);
        });

        // Verificerer at save() aldrig bliver kaldt
        verify(expenseRepository, never()).save(any());
    }

    // Tester at exception kastes hvis bilag mangler
    @Test
    void createExpense_shouldThrowException_whenReceiptIsMissing() {

        ExpenseRequestDTO dto = new ExpenseRequestDTO();

        dto.setTitle("Benzin");
        dto.setAmount(150);
        dto.setReceiptBase64("");

        AppUser user = new AppUser();

        // Forventer RuntimeException
        assertThrows(RuntimeException.class, () -> {

            expenseService.createExpense(dto, user);
        });

        // Verificerer at save() aldrig bliver kaldt
        verify(expenseRepository, never()).save(any());
    }
}