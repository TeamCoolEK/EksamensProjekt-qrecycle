package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.ExpenseRequestDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Expense;
import org.example.eksamensprojektqrecycle.repository.ExpenseRepository;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseServiceTest {

    private final ExpenseRepository expenseRepository =
            mock(ExpenseRepository.class);

    private final UserRepository userRepository =
            mock(UserRepository.class);

    private final ExpenseService expenseService =
            new ExpenseService(expenseRepository, userRepository);

    @Test
    void createExpense_shouldSaveExpense_whenDataIsValid() {

        ExpenseRequestDTO dto = new ExpenseRequestDTO();
        dto.setTitle("Benzin");
        dto.setAmount(150);
        dto.setReceiptBase64("data:image/png;base64,test");

        AppUser user = new AppUser();
        user.setUsername("driver1");

        when(userRepository.findByUsername("driver1"))
                .thenReturn(Optional.of(user));

        expenseService.createExpense(dto, "driver1");

        ArgumentCaptor<Expense> captor =
                ArgumentCaptor.forClass(Expense.class);

        verify(expenseRepository).save(captor.capture());

        Expense savedExpense = captor.getValue();

        assertEquals("Benzin", savedExpense.getTitle());
        assertEquals(150, savedExpense.getAmount());
        assertEquals("data:image/png;base64,test", savedExpense.getReceiptBase64());
        assertEquals(user, savedExpense.getUser());
        assertNotNull(savedExpense.getDate());

        verify(userRepository).findByUsername("driver1");
    }

    @Test
    void createExpense_shouldThrowException_whenTitleIsEmpty() {

        ExpenseRequestDTO dto = new ExpenseRequestDTO();
        dto.setTitle("");
        dto.setAmount(150);
        dto.setReceiptBase64("data:image/png;base64,test");

        assertThrows(RuntimeException.class, () -> {
            expenseService.createExpense(dto, "driver1");
        });

        verify(userRepository, never()).findByUsername(any());
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void createExpense_shouldThrowException_whenAmountIsZero() {

        ExpenseRequestDTO dto = new ExpenseRequestDTO();
        dto.setTitle("Benzin");
        dto.setAmount(0);
        dto.setReceiptBase64("data:image/png;base64,test");

        assertThrows(RuntimeException.class, () -> {
            expenseService.createExpense(dto, "driver1");
        });

        verify(userRepository, never()).findByUsername(any());
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void createExpense_shouldThrowException_whenReceiptIsMissing() {

        ExpenseRequestDTO dto = new ExpenseRequestDTO();
        dto.setTitle("Benzin");
        dto.setAmount(150);
        dto.setReceiptBase64("");

        assertThrows(RuntimeException.class, () -> {
            expenseService.createExpense(dto, "driver1");
        });

        verify(userRepository, never()).findByUsername(any());
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void createExpense_shouldThrowException_whenUserDoesNotExist() {

        ExpenseRequestDTO dto = new ExpenseRequestDTO();
        dto.setTitle("Benzin");
        dto.setAmount(150);
        dto.setReceiptBase64("data:image/png;base64,test");

        when(userRepository.findByUsername("driver1"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            expenseService.createExpense(dto, "driver1");
        });

        assertEquals("Bruger blev ikke fundet", exception.getMessage());

        verify(userRepository).findByUsername("driver1");
        verify(expenseRepository, never()).save(any());
    }
}