package org.example.eksamensprojektqrecycle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eksamensprojektqrecycle.model.dto.BusinessResponseDTO;
import org.example.eksamensprojektqrecycle.model.dto.CreateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.dto.GetExpensesDTO;
import org.example.eksamensprojektqrecycle.model.dto.UpdateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Tester kun AdminController
@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private BusinessService businessService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private StatisticService statisticService;

    @MockitoBean
    private PickupService pickupService;

    @MockitoBean
    private ExpenseService expenseService;

    @Test
    void getAllBusinesses_shouldReturnAllBusinesses() throws Exception {

        BusinessResponseDTO business1 = new BusinessResponseDTO(
                1,
                "Franks Pizza",
                "Frank",
                "28123456",
                "Nørrebrogade 12"
        );

        BusinessResponseDTO business2 = new BusinessResponseDTO(
                2,
                "Burger House",
                "Hans",
                "30112233",
                "Amagerbrogade 45"
        );

        when(businessService.getAllBusinesses())
                .thenReturn(List.of(business1, business2));

        mockMvc.perform(get("/admin/businesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].companyName").value("Franks Pizza"))
                .andExpect(jsonPath("$[0].contactPerson").value("Frank"))
                .andExpect(jsonPath("$[0].phoneNumber").value("28123456"))
                .andExpect(jsonPath("$[0].address").value("Nørrebrogade 12"))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].companyName").value("Burger House"))
                .andExpect(jsonPath("$[1].contactPerson").value("Hans"))
                .andExpect(jsonPath("$[1].phoneNumber").value("30112233"))
                .andExpect(jsonPath("$[1].address").value("Amagerbrogade 45"));

        verify(businessService).getAllBusinesses();
    }

    @Test
    void getAllBusinesses_shouldReturnEmptyList_whenNoBusinessesExist() throws Exception {

        when(businessService.getAllBusinesses())
                .thenReturn(List.of());

        mockMvc.perform(get("/admin/businesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(businessService).getAllBusinesses();
    }

    @Test
    void createBusiness_shouldReturnCreatedBusiness_whenDataIsValid() throws Exception {

        CreateBusinessDTO dto = new CreateBusinessDTO();
        dto.setCompanyName("Franks Pizza");
        dto.setContactPerson("Frank");
        dto.setPhoneNumber("28123456");
        dto.setAddress("Nørrebrogade 12");
        dto.setUsername("frankspizza");

        Business createdBusiness = new Business();
        createdBusiness.setId(1);
        createdBusiness.setCompanyName("Franks Pizza");
        createdBusiness.setContactPerson("Frank");
        createdBusiness.setPhoneNumber("28123456");
        createdBusiness.setAddress("Nørrebrogade 12");

        when(businessService.createBusiness(any(CreateBusinessDTO.class)))
                .thenReturn(createdBusiness);

        mockMvc.perform(post("/admin/businesses")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.companyName").value("Franks Pizza"))
                .andExpect(jsonPath("$.contactPerson").value("Frank"))
                .andExpect(jsonPath("$.phoneNumber").value("28123456"))
                .andExpect(jsonPath("$.address").value("Nørrebrogade 12"));

        verify(businessService).createBusiness(any(CreateBusinessDTO.class));
    }

    @Test
    void updateBusiness_shouldReturnUpdatedBusiness_whenDataIsValid() throws Exception {

        UpdateBusinessDTO dto = new UpdateBusinessDTO();
        dto.setCompanyName("Franks Pizza Updated");
        dto.setContactPerson("Frank Hansen");
        dto.setPhoneNumber("30112233");
        dto.setAddress("Amagerbrogade 45");

        BusinessResponseDTO updatedBusiness = new BusinessResponseDTO(
                1,
                "Franks Pizza Updated",
                "Frank Hansen",
                "30112233",
                "Amagerbrogade 45"
        );

        when(businessService.updateBusiness(any(Integer.class), any(UpdateBusinessDTO.class)))
                .thenReturn(updatedBusiness);

        mockMvc.perform(put("/admin/businesses/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.companyName").value("Franks Pizza Updated"))
                .andExpect(jsonPath("$.contactPerson").value("Frank Hansen"))
                .andExpect(jsonPath("$.phoneNumber").value("30112233"))
                .andExpect(jsonPath("$.address").value("Amagerbrogade 45"));

        verify(businessService).updateBusiness(any(Integer.class), any(UpdateBusinessDTO.class));
    }

    @Test
    void updateBusiness_shouldReturnBadRequest_whenServiceThrowsException() throws Exception {

        UpdateBusinessDTO dto = new UpdateBusinessDTO();
        dto.setCompanyName("");
        dto.setContactPerson("Frank Hansen");
        dto.setPhoneNumber("30112233");
        dto.setAddress("Amagerbrogade 45");

        when(businessService.updateBusiness(any(Integer.class), any(UpdateBusinessDTO.class)))
                .thenThrow(new RuntimeException("Virksomhedsnavn mangler"));

        mockMvc.perform(put("/admin/businesses/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isBadRequest());

        verify(businessService).updateBusiness(any(Integer.class), any(UpdateBusinessDTO.class));
    }

    @Test
    void deleteBusiness_shouldReturnOk_whenBusinessExists() throws Exception {

        mockMvc.perform(delete("/admin/businesses/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Virksomhed slettet"));

        verify(businessService).deleteBusiness(1);
    }

    @Test
    void deleteBusiness_shouldReturnBadRequest_whenBusinessDoesNotExist() throws Exception {

        doThrow(new RuntimeException("Virksomhed blev ikke fundet"))
                .when(businessService)
                .deleteBusiness(1);

        mockMvc.perform(delete("/admin/businesses/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Virksomhed blev ikke fundet"));

        verify(businessService).deleteBusiness(1);
    }

    @Test
    void getAllExpenses_shouldReturnAllExpenses() throws Exception {

        // Opretter to test-udgifter med kendte værdier
        var expense1 = new GetExpensesDTO();
        expense1.setAmount(150.75);
        expense1.setDate(LocalDate.of(2024, 3, 15));
        expense1.setReceiptBase64("base64encodedstring1");
        expense1.setTitle("Diesel");
        expense1.setUserId(1);

        GetExpensesDTO expense2 = new GetExpensesDTO();
        expense2.setAmount(300.00);
        expense2.setDate(LocalDate.of(2024, 4, 20));
        expense2.setReceiptBase64("base64encodedstring2");
        expense2.setTitle("Arbejdshandsker");
        expense2.setUserId(2);

        // Simulerer at service returnerer begge udgifter
        when(expenseService.getAllExpenses())
                .thenReturn(List.of(expense1, expense2));

        // Udfører GET-request og verificerer at begge udgifter returneres korrekt
        mockMvc.perform(get("/admin/business/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(150.75))
                .andExpect(jsonPath("$[0].date").value("2024-03-15"))
                .andExpect(jsonPath("$[0].receiptBase64").value("base64encodedstring1"))
                .andExpect(jsonPath("$[0].title").value("Diesel"))
                .andExpect(jsonPath("$[0].userId").value(1))

                .andExpect(jsonPath("$[1].amount").value(300.00))
                .andExpect(jsonPath("$[1].date").value("2024-04-20"))
                .andExpect(jsonPath("$[1].receiptBase64").value("base64encodedstring2"))
                .andExpect(jsonPath("$[1].title").value("Arbejdshandsker"))
                .andExpect(jsonPath("$[1].userId").value(2));

        // Bekræfter at service-metoden blev kaldt præcis én gang
        verify(expenseService).getAllExpenses();
    }

    @Test
    void getAllExpenses_shouldReturnEmptyList_whenNoExpensesExist() throws Exception {

        // Simulerer at der ingen udgifter findes i systemet
        when(expenseService.getAllExpenses())
                .thenReturn(List.of());

        // Udfører GET-request og verificerer at svaret er en tom liste med status 200
        mockMvc.perform(get("/admin/business/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        // Bekræfter at service-metoden blev kaldt præcis én gang
        verify(expenseService).getAllExpenses();
    }
}