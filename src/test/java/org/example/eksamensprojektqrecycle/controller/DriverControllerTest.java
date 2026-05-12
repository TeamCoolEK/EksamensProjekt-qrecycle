package org.example.eksamensprojektqrecycle.controller;

import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.model.enums.Status;
import org.example.eksamensprojektqrecycle.service.PickupService;
import tools.jackson.databind.ObjectMapper;
import org.example.eksamensprojektqrecycle.model.dto.ExpenseRequestDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


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

    @MockitoBean
    private PickupService pickupService;
    // Fake PickupService så vi ikke rammer databasen


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
        mockMvc.perform(post("/driver/expenses")
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
        mockMvc.perform(post("/driver/expenses")
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


    //Test af der returneres en liste med aktive afhentninger
    @Test
    void getActiveCollections_returnsList() throws Exception {

        // Opret en fake virksomhed med alle felter
        Business business = new Business();
        business.setCompanyName("Genbrug Nord ApS");
        business.setContactPerson("John Doe");
        business.setPhoneNumber("12345678");
        business.setAddress("Nørrebrogade 12, 2200 København N");

        // Opret en fake afhentning
        Collection collection = new Collection();
        collection.setId(1);
        collection.setStatus(Status.KLAR);
        collection.setBusiness(business);

        // Når getActiveCollections() kaldes — returner fake liste
        when(pickupService.getActiveCollections()).thenReturn(List.of(collection));

        // Udfør GET request og tjek svaret
        mockMvc.perform(get("/driver/collections/active"))
                .andExpect(status().isOk())
                // Tjek at svaret er en liste med ét element
                .andExpect(jsonPath("$.length()").value(1))
                // Tjek at businessName er korrekt
                .andExpect(jsonPath("$[0].businessName").value("Genbrug Nord ApS"))
                // Tjek at adresse er korrekt
                .andExpect(jsonPath("$[0].address").value("Nørrebrogade 12, 2200 København N"))
                // Tjek at status er korrekt
                .andExpect(jsonPath("$[0].status").value("KLAR"));
    }

    @Test
    void getActiveCollections_ingenAktiveAfhentninger_returnerTomListe() throws Exception {

        // Når der ingen aktive afhentninger er
        when(pickupService.getActiveCollections()).thenReturn(List.of());

        mockMvc.perform(get("/driver/collections/active"))
                .andExpect(status().isOk())
                // Tjek at svaret er en tom liste
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void completeCollection_gyldigtId_returnerOk() throws Exception {

        // Udfør PUT request med driverBags = 5
        mockMvc.perform(put("/driver/collections/1/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "driverBags": 5
                    }
                """))
                .andExpect(status().isOk())
                // Tjek bekræftelsesteksten
                .andExpect(content().string("Afhentning afsluttet"));

        // Tjek at completeCollection blev kaldt med id=1 og bags=5
        verify(pickupService, times(1)).completeCollection(1, 5);
    }

    @Test
    void completeCollection_ugyldigt_id_returnerFejl() throws Exception {

        // Simuler at afhentningen ikke findes i databasen
        doThrow(new RuntimeException("Afhentning ikke fundet"))
                .when(pickupService).completeCollection(999, 5);

        mockMvc.perform(put("/driver/collections/999/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "driverBags": 5
                }
            """))
                // Tjek at svaret er 500
                .andExpect(status().isInternalServerError())
                // Tjek at fejlbeskeden er korrekt
                .andExpect(content().string("Afhentning ikke fundet"));
    }

    /*år der ingen KLAR afhentninger er, returnerer endpoint en tom liste
    i stedet for at vise afhentninger med andre statusser. */
    @Test
    void getActiveCollections_ikkeKlarVises_ikke() throws Exception {
        // Simuler at databasen returnerer en tom liste — ingen KLAR afhentninger
        when(pickupService.getActiveCollections()).thenReturn(List.of());

        // Send GET request til endpointet
        mockMvc.perform(get("/driver/collections/active"))
                // Forventer 200 OK — ikke en fejl selvom listen er tom
                .andExpect(status().isOk())
                // Forventer at svaret indeholder 0 elementer
                .andExpect(jsonPath("$.length()").value(0));
    }

}
