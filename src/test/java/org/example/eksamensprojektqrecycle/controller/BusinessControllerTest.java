package org.example.eksamensprojektqrecycle.controller;

import org.example.eksamensprojektqrecycle.model.dto.CreateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.dto.UpdateCollectionStatusDTO;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.model.entity.Business;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Enhedstest for BusinessController.
 * Dækker tre endpoints:
 * - POST  /business/afhentning/klar
 * - POST  /business/businesses
 * - GET   /business/afhentning/{id}
 */
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

    /**
     * Test: markCollectionReady success
     * Endpoint: POST /business/afhentning/klar
     * Forventet: HTTP 200 og en Collection i responsen
     */
    @Test
    void markCollectionReady_success() throws Exception {
        // Forbered DTO som request body
        UpdateCollectionStatusDTO dto = new UpdateCollectionStatusDTO(3, 2); // collectionId=3, businessBags=2

        // Mock return fra service-laget
        Collection updated = new Collection();
        updated.setId(3);

        when(pickupService.markReadyForPickup(any(UpdateCollectionStatusDTO.class))).thenReturn(updated);

        mockMvc.perform(post("/business/afhentning/klar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }

    /**
     * Test: markCollectionReady badRequest
     * Endpoint: POST /business/afhentning/klar
     * Forventet: HTTP 400 og fejlbesked når service kaster RuntimeException
     */
    @Test
    void markCollectionReady_badRequest() throws Exception {
        UpdateCollectionStatusDTO dto = new UpdateCollectionStatusDTO(3, 0); // ugyldigt scenarie

        when(pickupService.markReadyForPickup(any(UpdateCollectionStatusDTO.class)))
                .thenThrow(new RuntimeException("Invalid data"));

        mockMvc.perform(post("/business/afhentning/klar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid data"));
    }

    /**
     * Test: getCollection success
     * Endpoint: GET /business/afhentning/{id}
     * Forventet: HTTP 200 og den ønskede Collection
     */
    @Test
    void getCollection_success() throws Exception {
        Collection collection = new Collection();
        collection.setId(5);

        when(pickupService.getCollectionById(5)).thenReturn(collection);

        mockMvc.perform(get("/business/afhentning/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    /**
     * Test: getCollection notFound
     * Endpoint: GET /business/afhentning/{id}
     * Forventet: HTTP 404 og fejlbesked hvis collection ikke findes
     */
    @Test
    void getCollection_notFound() throws Exception {
        when(pickupService.getCollectionById(999)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/business/afhentning/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not found"));
    }
}