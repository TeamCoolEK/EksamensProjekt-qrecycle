package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.ExpenseRequestDTO;
import org.example.eksamensprojektqrecycle.model.dto.GetExpensesDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Expense;
import org.example.eksamensprojektqrecycle.repository.ExpenseRepository;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
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

//    @Test
//    void createExpense_shouldSaveExpense_whenDataIsValid() {
//
//        ExpenseRequestDTO dto = new ExpenseRequestDTO();
//        dto.setTitle("Benzin");
//        dto.setAmount(150);
//        dto.setReceiptBase64("data:image/png;base64,test");
//
//        AppUser user = new AppUser();
//        user.setUsername("driver1");
//
//        when(userRepository.findByUsername("driver1"))
//                .thenReturn(Optional.of(user));
//
//        expenseService.createExpense(dto, "driver1");
//
//        ArgumentCaptor<Expense> captor =
//                ArgumentCaptor.forClass(Expense.class);
//
//        verify(expenseRepository).save(captor.capture());
//
//        Expense savedExpense = captor.getValue();
//
//        assertEquals("Benzin", savedExpense.getTitle());
//        assertEquals(150, savedExpense.getAmount());
//        assertEquals("data:image/png;base64,test", savedExpense.getReceiptBase64());
//        assertEquals(user, savedExpense.getUser());
//        assertNotNull(savedExpense.getDate());
//
//        verify(userRepository).findByUsername("driver1");
//    }

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

    @Test
    void getAllExpenses_ReturnsMappedDTOList() {
        // Opretter en bruger med et bestemt id
        AppUser user = new AppUser();
        user.setId(1);

        // Opretter en udgift med alle felter udfyldt
        Expense expense = new Expense();
        expense.setAmount(100.0);
        expense.setDate(LocalDate.of(2024, 1, 15));
        expense.setReceiptBase64("base64string");
        expense.setTitle("Frokost");
        expense.setUser(user);

        // Simulerer at repository returnerer én udgift
        when(expenseRepository.findAll()).thenReturn(List.of(expense));

        // Kalder metoden der testes
        List<GetExpensesDTO> result = expenseService.getAllExpenses();

        // Tjekker at listen indeholder ét element og at alle felter er korrekt mappet
        assertEquals(1, result.size());
        GetExpensesDTO dto = result.get(0);
        assertEquals(100.0, dto.getAmount());
        assertEquals(LocalDate.of(2024, 1, 15), dto.getDate());
        assertEquals("base64string", dto.getReceiptBase64());
        assertEquals("Frokost", dto.getTitle());
        assertEquals(1L, dto.getUserId());
    }

    @Test
    void getAllExpenses_WhenNoExpenses_ReturnsEmptyList() {
        // Simulerer at repository ikke finder nogen udgifter
        when(expenseRepository.findAll()).thenReturn(List.of());

        // Kalder metoden der testes
        List<GetExpensesDTO> result = expenseService.getAllExpenses();

        // Tjekker at den returnerede liste er tom
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllExpenses_WithMultipleExpenses_ReturnsAllMapped() {
        // Opretter en bruger der deles mellem begge udgifter
        AppUser user = new AppUser();
        user.setId(1);

        // Opretter første udgift
        Expense firstExpense = new Expense();
        firstExpense.setAmount(100.0);
        firstExpense.setDate(LocalDate.of(2024, 1, 15));
        firstExpense.setReceiptBase64("base64string");
        firstExpense.setTitle("Frokost");
        firstExpense.setUser(user);

        // Opretter anden udgift
        Expense secondExpense = new Expense();
        secondExpense.setAmount(200.0);
        secondExpense.setDate(LocalDate.of(2024, 2, 20));
        secondExpense.setReceiptBase64("andenBase64");
        secondExpense.setTitle("Taxa");
        secondExpense.setUser(user);

        // Simulerer at repository returnerer begge udgifter
        when(expenseRepository.findAll()).thenReturn(List.of(firstExpense, secondExpense));

        // Kalder metoden der testes
        List<GetExpensesDTO> result = expenseService.getAllExpenses();

        // Tjekker at begge udgifter er mappet korrekt og i den rigtige rækkefølge
        assertEquals(2, result.size());
        assertEquals("Frokost", result.get(0).getTitle());
        assertEquals("Taxa", result.get(1).getTitle());
    }
}
