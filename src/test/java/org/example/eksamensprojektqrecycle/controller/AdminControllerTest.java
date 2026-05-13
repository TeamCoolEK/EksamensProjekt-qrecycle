package org.example.eksamensprojektqrecycle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eksamensprojektqrecycle.model.dto.CreateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.dto.UpdateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.service.BusinessService;
import org.example.eksamensprojektqrecycle.service.StatisticService;
import org.example.eksamensprojektqrecycle.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

// Tester kun AdminController
@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {


    @Autowired
    private MockMvc mockMvc; // Bruges til at sende fake HTTP requests til controlleren

    private final ObjectMapper objectMapper = new ObjectMapper(); // Bruges til at konvertere Java objekter til JSON

    // Mock version af Service klasserne
    @MockitoBean
    private BusinessService businessService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private StatisticService statisticService;

    // Tester at admin kan hente alle virksomheder
    @Test
    void getAllBusinesses_shouldReturnAllBusinesses() throws Exception {

        Business business1 = new Business(); // Opretter første test virksomhed

        // Sætter data på første virksomhed
        business1.setId(1);
        business1.setCompanyName("Franks Pizza");
        business1.setContactPerson("Frank");
        business1.setPhoneNumber("28123456");
        business1.setAddress("Nørrebrogade 12");

        Business business2 = new Business(); // Opretter anden test virksomhed

        // Sætter data på anden virksomhed
        business2.setId(2);
        business2.setCompanyName("Burger House");
        business2.setContactPerson("Hans");
        business2.setPhoneNumber("30112233");
        business2.setAddress("Amagerbrogade 45");

        // Fortæller mock service hvad den skal returnere
        when(businessService.getAllBusinesses())
                .thenReturn(List.of(business1, business2));


        mockMvc.perform(get("/admin/businesses")) // Sender GET request til endpointet
                .andExpect(status().isOk()) // Tjekker at HTTP status er 200 OK
                .andExpect(jsonPath("$[0].id").value(1)) // Tjekker første virksomheds id
                .andExpect(jsonPath("$[0].companyName").value("Franks Pizza")) // Tjekker første virksomheds navn
                .andExpect(jsonPath("$[0].contactPerson").value("Frank")) // Tjekker første virksomheds kontaktperson
                .andExpect(jsonPath("$[0].phoneNumber").value("28123456")) // Tjekker første virksomheds telefonnummer
                .andExpect(jsonPath("$[0].address").value("Nørrebrogade 12")) // Tjekker første virksomheds adresse

                .andExpect(jsonPath("$[1].id").value(2)) // Tjekker anden virksomheds id
                .andExpect(jsonPath("$[1].companyName").value("Burger House")) // Tjekker anden virksomheds navn
                .andExpect(jsonPath("$[1].contactPerson").value("Hans")) // Tjekker anden virksomheds kontaktperson
                .andExpect(jsonPath("$[1].phoneNumber").value("30112233")) // Tjekker anden virksomheds telefonnummer
                .andExpect(jsonPath("$[1].address").value("Amagerbrogade 45")); // Tjekker anden virksomheds adresse

        verify(businessService).getAllBusinesses(); // Verificerer at service-metoden blev kaldt
    }

    // Tester at endpointet returnerer en tom liste hvis der ikke findes virksomheder
    @Test
    void getAllBusinesses_shouldReturnEmptyList_whenNoBusinessesExist() throws Exception {

        // Fortæller mock service at den skal returnere en tom liste
        when(businessService.getAllBusinesses())
                .thenReturn(List.of());

        // Sender GET request til endpointet
        mockMvc.perform(get("/admin/businesses"))
                .andExpect(status().isOk()) // Tjekker at HTTP status er 200 OK
                .andExpect(jsonPath("$").isArray()) // Tjekker at response er et array
                .andExpect(jsonPath("$").isEmpty()); // Tjekker at arrayet er tomt

        verify(businessService).getAllBusinesses(); // Verificerer at service-metoden blev kaldt
    }

    // Tester at admin kan oprette en virksomhed
    @Test
    void createBusiness_shouldReturnCreatedBusiness_whenDataIsValid() throws Exception {

        // Opretter DTO med input-data
        CreateBusinessDTO dto = new CreateBusinessDTO();

        // Sætter data på DTO
        dto.setCompanyName("Franks Pizza");
        dto.setContactPerson("Frank");
        dto.setPhoneNumber("28123456");
        dto.setAddress("Nørrebrogade 12");
        dto.setUsername("frankspizza");

        // Opretter virksomhed som mock service skal returnere
        Business createdBusiness = new Business();

        // Sætter data på den oprettede virksomhed
        createdBusiness.setId(1);
        createdBusiness.setCompanyName("Franks Pizza");
        createdBusiness.setContactPerson("Frank");
        createdBusiness.setPhoneNumber("28123456");
        createdBusiness.setAddress("Nørrebrogade 12");

        // Fortæller mock service hvad den skal returnere ved oprettelse
        when(businessService.createBusiness(any(CreateBusinessDTO.class)))
                .thenReturn(createdBusiness);

        // Sender POST request til endpointet
        mockMvc.perform(post("/admin/businesses")

                        .contentType("application/json") // Fortæller at request body er JSON
                        .content(objectMapper.writeValueAsString(dto))) // Konverterer DTO til JSON og sender som body

                .andExpect(status().isOk()) // Tjekker at HTTP status er 200 OK
                .andExpect(jsonPath("$.id").value(1)) // Tjekker id på oprettet virksomhed
                .andExpect(jsonPath("$.companyName").value("Franks Pizza")) // Tjekker navn på oprettet virksomhed
                .andExpect(jsonPath("$.contactPerson").value("Frank")) // Tjekker kontaktperson på oprettet virksomhed
                .andExpect(jsonPath("$.phoneNumber").value("28123456")) // Tjekker telefonnummer på oprettet virksomhed
                .andExpect(jsonPath("$.address").value("Nørrebrogade 12")); // Tjekker adresse på oprettet virksomhed

        // Verificerer at createBusiness blev kaldt i service
        verify(businessService).createBusiness(any(CreateBusinessDTO.class));
    }

    // Tester at admin kan opdatere virksomhed
    @Test
    void updateBusiness_shouldReturnUpdatedBusiness_whenDataIsValid() throws Exception {

        // Opretter DTO med opdaterede oplysninger
        UpdateBusinessDTO dto = new UpdateBusinessDTO();
        dto.setCompanyName("Franks Pizza Updated");
        dto.setContactPerson("Frank Hansen");
        dto.setPhoneNumber("30112233");
        dto.setAddress("Amagerbrogade 45");

        // Opretter opdateret virksomhed
        Business updatedBusiness = new Business();
        updatedBusiness.setId(1);
        updatedBusiness.setCompanyName("Franks Pizza Updated");
        updatedBusiness.setContactPerson("Frank Hansen");
        updatedBusiness.setPhoneNumber("30112233");
        updatedBusiness.setAddress("Amagerbrogade 45");

        // Mock service
        when(businessService.updateBusiness(any(Integer.class), any(UpdateBusinessDTO.class)))
                .thenReturn(updatedBusiness);

        // Sender PUT request til endpoint
        mockMvc.perform(put("/admin/businesses/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))

                // Tjekker HTTP status
                .andExpect(status().isOk())

                // Tjekker response
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.companyName").value("Franks Pizza Updated"))
                .andExpect(jsonPath("$.contactPerson").value("Frank Hansen"))
                .andExpect(jsonPath("$.phoneNumber").value("30112233"))
                .andExpect(jsonPath("$.address").value("Amagerbrogade 45"));

        // Verificerer service kald
        verify(businessService).updateBusiness(any(Integer.class), any(UpdateBusinessDTO.class));
    }

    // Tester fejl hvis service kaster exception
    @Test
    void updateBusiness_shouldReturnBadRequest_whenServiceThrowsException() throws Exception {

        // Opretter DTO
        UpdateBusinessDTO dto = new UpdateBusinessDTO();
        dto.setCompanyName("");
        dto.setContactPerson("Frank Hansen");
        dto.setPhoneNumber("30112233");
        dto.setAddress("Amagerbrogade 45");

        // Mock service fejl
        when(businessService.updateBusiness(any(Integer.class), any(UpdateBusinessDTO.class)))
                .thenThrow(new RuntimeException("Virksomhedsnavn mangler"));

        // Sender PUT request
        mockMvc.perform(put("/admin/businesses/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))

                // Tjekker status 400
                .andExpect(status().isBadRequest());

        // Verificerer service kald
        verify(businessService).updateBusiness(any(Integer.class), any(UpdateBusinessDTO.class));
    }

    // Tester at admin kan slette virksomhed
    @Test
    void deleteBusiness_shouldReturnOk_whenBusinessExists() throws Exception {

        // Sender DELETE request til endpoint
        mockMvc.perform(delete("/admin/businesses/1"))

                // Tjekker at HTTP status er 200 OK
                .andExpect(status().isOk())

                // Tjekker response besked
                .andExpect(content().string("Virksomhed slettet"));

        // Verificerer at service-metoden blev kaldt
        verify(businessService).deleteBusiness(1);
    }

    // Tester fejl hvis virksomheden ikke findes
    @Test
    void deleteBusiness_shouldReturnBadRequest_whenBusinessDoesNotExist() throws Exception {

        // Mock service til at kaste fejl
        doThrow(new RuntimeException("Virksomhed blev ikke fundet"))
                .when(businessService)
                .deleteBusiness(1);

        // Sender DELETE request til endpoint
        mockMvc.perform(delete("/admin/businesses/1"))

                // Tjekker at HTTP status er 400 Bad Request
                .andExpect(status().isBadRequest())

                // Tjekker fejlbesked
                .andExpect(content().string("Virksomhed blev ikke fundet"));

        // Verificerer at service-metoden blev kaldt
        verify(businessService).deleteBusiness(1);
    }
}