package org.example.eksamensprojektqrecycle.controller;

import org.example.eksamensprojektqrecycle.model.dto.UpdateCollectionStatusDTO;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.model.enums.Status;
import org.example.eksamensprojektqrecycle.service.BusinessService;
import org.example.eksamensprojektqrecycle.service.PickupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Integration tests for BusinessController.
//Tester HTTP endpoints og integration mellem controller og service - bruger MockMvc til at simulere HTTP  requests//
//Dækker tre endpoints:
//- POST  /business/afhentning/klar
//- POST  /business/businesses
//- GET   /business/afhentning/{id}//
@WebMvcTest(BusinessController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable CSRF/sikkerhedsfiltre i tests
class BusinessControllerTest {

    // MockMvc bruges til at simulere HTTP requests mod controlleren
    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper bruges til at serialisere DTO'er til JSON i requests
    @Autowired
    private ObjectMapper objectMapper;

    // MockBean: Spring håndterer injection af mocks til controlleren
    @MockitoBean
    private BusinessService businessService;

    @MockitoBean
    private PickupService pickupService;

    //Tests for markér  klar endpoint//

    // Test: markCollectionReady success
    //Endpoint: POST /business/afhentning/klar
    //Forventet: HTTP 200 og en Collection i responsen

    @Test
    void markCollectionReady_success() throws Exception {
        // Forbered DTO som request body
        UpdateCollectionStatusDTO dto = new UpdateCollectionStatusDTO(3, 2); // collectionId=3, businessBags=2

        // Mock return fra service-laget
        Collection updated = new Collection();
        updated.setId(3);
        updated.setStatus(Status.KLAR);
        updated.setBusinessBags(2);

        when(pickupService.markReadyForPickup(any(UpdateCollectionStatusDTO.class))).thenReturn(updated);

        mockMvc.perform(post("/business/afhentning/klar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.status").value("KLAR"))
                .andExpect(jsonPath("$.businessBags").value(2));
    }

    //Test: markCollectionReady badRequest
    //Endpoint: POST /business/afhentning/klar
    //Forventet: HTTP 400 og fejlbesked når service kaster RuntimeException

    @Test
    void markCollectionReady_badRequest() throws Exception {
        UpdateCollectionStatusDTO dto = new UpdateCollectionStatusDTO(3, 0); // ugyldigt scenarie

        when(pickupService.markReadyForPickup(any(UpdateCollectionStatusDTO.class)))
                .thenThrow(new RuntimeException("Antal poser skal være mindst 1."));

        mockMvc.perform(post("/business/afhentning/klar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Antal poser skal være mindst 1."));
    }

    //Tests for get collection endpoint//

    //Test: getCollection success
    //Endpoint: GET /business/afhentning/{id}
    //Forventet: HTTP 200 og den ønskede Collection

    @Test
    void getCollection_success() throws Exception {
        Collection collection = new Collection();
        collection.setId(5);
        collection.setStatus(Status.IKKE_KLAR);

        when(pickupService.getCollectionById(5)).thenReturn(collection);

        mockMvc.perform(get("/business/afhentning/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("IKKE_KLAR"));
    }

    //Test: getCollection notFound
    //Endpoint: GET /business/afhentning/{id}
    //Forventet: HTTP 404 og fejlbesked hvis collection ikke findes

    @Test
    void getCollection_notFound() throws Exception {
        when(pickupService.getCollectionById(999)).thenThrow(new RuntimeException("Afhentningen blev ikke fundet i systemet."));

        mockMvc.perform(get("/business/afhentning/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Afhentningen blev ikke fundet i systemet."));
    }

    //Tests for annullering endpoint//

    //QE-114, QE-120: test succesfuld annullering via endpoint//
    //Endpoint: POST /business/afhentning/{id}/annuller
    //Forventet: HTTP 200 og opdateret Collection med status IKKE_KLAR

    @Test
    void cancelPickup_success() throws Exception {
        //Arrange: Mock serviice til a returnere annulleret collection
        Collection cancelledCollection = new Collection();
        cancelledCollection.setId(1);
        cancelledCollection.setStatus(Status.IKKE_KLAR);
        cancelledCollection.setBusinessBags(5);
        cancelledCollection.setDate(LocalDate.now());

        when(pickupService.cancelPickup(1)).thenReturn(cancelledCollection);

        //Act & Assert: POST til annullerings-endpoint
        mockMvc.perform(post("/business/afhentning/1/annuller")
                .contentType((MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("IKKE_KLAR"))
                .andExpect(jsonPath("$.businessBags").value(5));


        //Verificer a service blev kaldt
        verify(pickupService, times(1)).cancelPickup(1);
    }

    //QE-125: test at endpoint returnerer 400 ved ugyldig status//
    //Endpoint: POST /business/afhentning/{id}/annuller
    //Forventet: HTTP 400 og fejlbesked når collection ikke har status KLAR

    @Test
    void cancelPickup_badReques_whenStatusNotKlar() throws Exception {
        // Arrange: Mock service til at kaste exception
        when(pickupService.cancelPickup(2))
                .thenThrow(new RuntimeException(
                        "Kun afhentninger med status 'Klar' kan annulleres. " +
                        "Nuværende status er: IKKE_KLAR"));

        //Act & Assert: POST til endpoint
        mockMvc.perform(post("/business/afhentning/2/annuller")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("IKKE_KLAR")));

        //Verificer at service blev  kaldt//
        verify(pickupService,times(1)).cancelPickup(2);
    }

    //Test at endpoint returnerer 400 når collection ikke findes//
    //Endpoint: POST /business/afhentning/{id}/annuller
    //Forventet: HTTP 400 og fejlbesked

    @Test
    void cancelPickup_badRequest_whenNotFound() throws Exception {
        //Arrange: Mock service til at kaste exception
        when(pickupService.cancelPickup(999))
                .thenThrow(new RuntimeException(
                        "Afhentningen blev ikke fundet i systemet."));

        // act & assert
        mockMvc.perform(post("/business/afhentning/999/annuller")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("Afhentningen blev ikke fundet i systemet.")));

        verify(pickupService,times(1)).cancelPickup(999);
    }


    //Test at endpoint returnerer 400 når collection har status AFHENTET//
    //Endpoint: POST /business/afhentning/{id}/annuller
    //Forventet: HTTP 400 og fejlbesked

    @Test
    void cancelPickup_badRequest_whenStatusAfhentet() throws Exception {
        //Arrange: Mock service til at kaste exception
        when(pickupService.cancelPickup(3))
                .thenThrow(new RuntimeException(
                        "Kun afhentninger med status 'Klar' kan annulleres. " +
                                "Nuværende status: AFHENTET"));

        //Act & assert
        mockMvc.perform(post("/business/afhentning/3/annuller")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("AFHENTET")));

        verify(pickupService,times(1)).cancelPickup(3);
    }
}

