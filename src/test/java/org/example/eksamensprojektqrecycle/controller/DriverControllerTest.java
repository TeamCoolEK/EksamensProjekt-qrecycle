package org.example.eksamensprojektqrecycle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eksamensprojektqrecycle.model.dto.DriverLocationDTO;
import org.example.eksamensprojektqrecycle.model.dto.ExpenseRequestDTO;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.model.enums.Status;
import org.example.eksamensprojektqrecycle.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriverController.class)
@AutoConfigureMockMvc(addFilters = false)
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ExpenseService expenseService;

    @MockitoBean
    private PickupService pickupService;

    @MockitoBean
    private DriverLocationService driverLocationService;

    @MockitoBean
    private EndStopService endStopService;

    @Test
    void createExpense_shouldReturnOk_whenUserIsLoggedIn() throws Exception {

        ExpenseRequestDTO dto = new ExpenseRequestDTO();
        dto.setTitle("Benzin");
        dto.setAmount(150);
        dto.setReceiptBase64("data:image/png;base64,test");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("driver1", null, List.of());

        mockMvc.perform(post("/driver/expenses")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isOk())
                .andExpect(content().string("Udgift gemt"));

        verify(expenseService).createExpense(any(ExpenseRequestDTO.class), eq("driver1"));
    }

    @Test
    void createExpense_shouldReturnUnauthorized_whenUserIsNotLoggedIn() throws Exception {

        ExpenseRequestDTO dto = new ExpenseRequestDTO();
        dto.setTitle("Benzin");
        dto.setAmount(150);
        dto.setReceiptBase64("data:image/png;base64,test");

        mockMvc.perform(post("/driver/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Ikke logget ind"));

        verify(expenseService, never()).createExpense(any(), any());
    }

    @Test
    void getActiveCollections_returnsList() throws Exception {

        Business business = new Business();
        business.setCompanyName("Genbrug Nord ApS");
        business.setContactPerson("John Doe");
        business.setPhoneNumber("12345678");
        business.setAddress("Nørrebrogade 12, 2200 København N");

        Collection collection = new Collection();
        collection.setId(1);
        collection.setStatus(Status.KLAR);
        collection.setBusiness(business);

        when(pickupService.getActiveCollections()).thenReturn(List.of(collection));

        mockMvc.perform(get("/driver/collections/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].businessName").value("Genbrug Nord ApS"))
                .andExpect(jsonPath("$[0].address").value("Nørrebrogade 12, 2200 København N"))
                .andExpect(jsonPath("$[0].status").value("KLAR"));
    }

    @Test
    void getActiveCollections_ingenAktiveAfhentninger_returnerTomListe() throws Exception {

        when(pickupService.getActiveCollections()).thenReturn(List.of());

        mockMvc.perform(get("/driver/collections/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void completeCollection_gyldigtId_returnerOk() throws Exception {

        mockMvc.perform(put("/driver/collections/1/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "driverBags": 5
                                }
                                """))

                .andExpect(status().isOk())
                .andExpect(content().string("Afhentning afsluttet"));

        verify(pickupService, times(1)).completeCollection(1, 5);
    }

    @Test
    void completeCollection_ugyldigt_id_returnerFejl() throws Exception {

        doThrow(new RuntimeException("Afhentning ikke fundet"))
                .when(pickupService)
                .completeCollection(999, 5);

        mockMvc.perform(put("/driver/collections/999/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "driverBags": 5
                                }
                                """))

                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Afhentning ikke fundet"));
    }

    @Test
    void getActiveCollections_ikkeKlarVises_ikke() throws Exception {

        when(pickupService.getActiveCollections()).thenReturn(List.of());

        mockMvc.perform(get("/driver/collections/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Tester at POST /driver/location gemmer position og returnerer 200
    @Test
    void updateLocation_gyldigRequest_returnerOk() throws Exception {

        DriverLocationDTO dto = new DriverLocationDTO();
        dto.setLatitude(55.6761);
        dto.setLongitude(12.5683);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("driver1", null, List.of());

        mockMvc.perform(post("/driver/location")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isOk())
                .andExpect(content().string("Position opdateret"));

        verify(driverLocationService).updateLocation(eq("driver1"), any(DriverLocationDTO.class));
    }

    // Tester at GET /driver/location returnerer 404 når ingen position findes i hukommelsen
    @Test
    void getLocation_ingenPosition_returner404() throws Exception {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("driver1", null, List.of());

        when(driverLocationService.getLocation("driver1")).thenReturn(null);

        mockMvc.perform(get("/driver/location")
                        .principal(authentication))

                .andExpect(status().isNotFound())
                .andExpect(content().string("Ingen aktiv position fundet — genstart venligst sporing"));
    }

    @Test
    void removeLocation_gyldigRequest_returnerOk() throws Exception {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("driver1", null, List.of());

        mockMvc.perform(delete("/driver/location")
                        .principal(authentication))

                .andExpect(status().isOk())
                .andExpect(content().string("Position fjernet"));

        verify(driverLocationService).removeLocation(eq("driver1"));
    }
}

