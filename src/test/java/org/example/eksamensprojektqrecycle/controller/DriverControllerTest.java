package org.example.eksamensprojektqrecycle.controller;

import tools.jackson.databind.ObjectMapper;
import org.example.eksamensprojektqrecycle.model.dto.ExpenseRequestDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// Starter kun web/controller laget af Spring
@WebMvcTest(DriverController.class)
class DriverControllerTest {

    // Bruges til at simulere HTTP requests
    @Autowired
    private MockMvc mockMvc;

    // Konverterer Java objekter til JSON
    @Autowired
    private ObjectMapper objectMapper;

    // Mock version af ExpenseService
    @MockitoBean
    private ExpenseService expenseService;


    // Tester at endpoint returnerer 200 OK
    // når bruger er logget ind
    @Test
    void createExpense_shouldReturnOk_whenUserIsLoggedIn() throws Exception {

        // Opretter test DTO
        ExpenseRequestDTO dto = new ExpenseRequestDTO();
        dto.setTitle("Benzin");
        dto.setAmount(150);
        dto.setReceiptBase64("data:image/png;base64,test");

        // Opretter fake bruger til session
        AppUser user = new AppUser();

        // Simulerer POST request til endpoint
        mockMvc.perform(post("/api/driver/expenses")

                        // Simulerer login session
                        .sessionAttr("user", user)

                        // Sender JSON request
                        .contentType("application/json")

                        // Konverterer DTO til JSON
                        .content(objectMapper.writeValueAsString(dto)))
                // Forventer HTTP 200
                .andExpect(status().isOk())
                // Forventer korrekt response text
                .andExpect(content().string("Udgift gemt"));

        // Verificerer at service blev kaldt
        verify(expenseService).createExpense(any(ExpenseRequestDTO.class), any(AppUser.class));
    }


    // Tester at endpoint returnerer 401
    // når bruger IKKE er logget ind
    @Test
    void createExpense_shouldReturnUnauthorized_whenUserIsNotLoggedIn()
            throws Exception {

        // Opretter test DTO
        ExpenseRequestDTO dto = new ExpenseRequestDTO();

        dto.setTitle("Benzin");

        dto.setAmount(150);

        dto.setReceiptBase64(
                "data:image/png;base64,test"
        );

        // Simulerer POST request uden session
        mockMvc.perform(post("/api/driver/expenses")

                        .contentType("application/json")

                        .content(
                                objectMapper
                                        .writeValueAsString(dto)
                        ))

                // Forventer HTTP 401
                .andExpect(status().isUnauthorized())

                // Forventer korrekt fejlbesked
                .andExpect(content().string("Ikke logget ind"));

        // Verificerer at service IKKE blev kaldt
        verify(expenseService, never())

                .createExpense(any(), any());
    }
}