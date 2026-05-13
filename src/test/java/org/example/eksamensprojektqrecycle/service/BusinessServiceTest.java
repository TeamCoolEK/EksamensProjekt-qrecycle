package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.CreateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.model.enums.Role;
import org.example.eksamensprojektqrecycle.repository.BusinessRepository;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusinessServiceTest {

    // Mock repository til virksomhed
    private final BusinessRepository businessRepository = mock(BusinessRepository.class);

    // Mock repository til brugere
    private final UserRepository userRepository = mock(UserRepository.class);

    // Service som testes
    private final BusinessService businessService =
            new BusinessService(businessRepository, userRepository);

    // Tester at virksomhed og bruger gemmes korrekt
    @Test
    void createBusiness_shouldSaveBusinessAndUser_whenDataIsValid() {

        // Opretter test DTO
        CreateBusinessDTO dto = new CreateBusinessDTO();

        dto.setCompanyName("Franks Pizza");
        dto.setContactPerson("Frank");
        dto.setPhoneNumber("28123456");
        dto.setAddress("Nørrebrogade 12");
        dto.setUsername("frankspizza");

        // Mock save på user repository
        when(userRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Mock save på business repository
        when(businessRepository.save(any(Business.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Kalder service metode
        Business createdBusiness = businessService.createBusiness(dto);

        // Captor til AppUser
        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);

        // Captor til Business
        ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);

        // Verificerer save kald
        verify(userRepository).save(userCaptor.capture());
        verify(businessRepository).save(businessCaptor.capture());

        // Henter gemt bruger
        AppUser savedUser = userCaptor.getValue();

        // Henter gemt virksomhed
        Business savedBusiness = businessCaptor.getValue();

        // Verificerer bruger data
        assertEquals("frankspizza", savedUser.getUsername());
        assertEquals(Role.BUSINESS, savedUser.getRole());

        // Tjekker at password er 4 cifre
        assertTrue(savedUser.getPassword().matches("\\d{4}"));

        // Verificerer virksomhedsdata
        assertEquals("Franks Pizza", savedBusiness.getCompanyName());
        assertEquals("Frank", savedBusiness.getContactPerson());
        assertEquals("28123456", savedBusiness.getPhoneNumber());
        assertEquals("Nørrebrogade 12", savedBusiness.getAddress());

        // Verificerer relation mellem virksomhed og bruger
        assertEquals(savedUser, savedBusiness.getAppUser());

        // Verificerer returneret objekt
        assertEquals(savedBusiness, createdBusiness);
    }

    // Tester fejl hvis virksomhedsnavn mangler
    @Test
    void createBusiness_shouldThrowException_whenCompanyNameIsEmpty() {

        // Opretter DTO med tomt navn
        CreateBusinessDTO dto =
                new CreateBusinessDTO();

        dto.setCompanyName("");
        dto.setContactPerson("Frank");
        dto.setPhoneNumber("28123456");
        dto.setAddress("Nørrebrogade 12");
        dto.setUsername("frankspizza");

        // Forventer exception
        assertThrows(RuntimeException.class, () -> {
            businessService.createBusiness(dto);
        });

        // Verificerer at intet gemmes
        verify(userRepository, never()).save(any());
        verify(businessRepository, never()).save(any());
    }

    // Tester fejl hvis username mangler
    @Test
    void createBusiness_shouldThrowException_whenUsernameIsEmpty() {

        // Opretter DTO med tomt username
        CreateBusinessDTO dto = new CreateBusinessDTO();

        dto.setCompanyName("Franks Pizza");
        dto.setContactPerson("Frank");
        dto.setPhoneNumber("28123456");
        dto.setAddress("Nørrebrogade 12");
        dto.setUsername("");

        // Forventer exception
        assertThrows(RuntimeException.class, () -> {
            businessService.createBusiness(dto);
        });

        // Verificerer at intet gemmes
        verify(userRepository, never()).save(any());
        verify(businessRepository, never()).save(any());
    }
    // Tester at alle virksomheder hentes fra repository
    @Test
    void getAllBusinesses_shouldReturnAllBusinesses() {

        // Opretter test virksomheder
        Business business1 = new Business();
        business1.setCompanyName("Franks Pizza");
        business1.setContactPerson("Frank");
        business1.setPhoneNumber("28123456");
        business1.setAddress("Nørrebrogade 12");

        Business business2 = new Business();
        business2.setCompanyName("Burger House");
        business2.setContactPerson("Hans");
        business2.setPhoneNumber("30112233");
        business2.setAddress("Amagerbrogade 45");

        // Mock repository
        when(businessRepository.findAll())
                .thenReturn(List.of(business1, business2));

        // Kalder service metode
        List<Business> businesses = businessService.getAllBusinesses();

        // Verificerer resultat
        assertEquals(2, businesses.size());

        assertEquals("Franks Pizza", businesses.get(0).getCompanyName());
        assertEquals("Frank", businesses.get(0).getContactPerson());
        assertEquals("28123456", businesses.get(0).getPhoneNumber());
        assertEquals("Nørrebrogade 12", businesses.get(0).getAddress());

        assertEquals("Burger House", businesses.get(1).getCompanyName());
        assertEquals("Hans", businesses.get(1).getContactPerson());
        assertEquals("30112233", businesses.get(1).getPhoneNumber());
        assertEquals("Amagerbrogade 45", businesses.get(1).getAddress());

        // Verificerer at repository blev kaldt
        verify(businessRepository).findAll();
    }

    // Tester at tom liste returneres hvis der ikke findes virksomheder
    @Test
    void getAllBusinesses_shouldReturnEmptyList_whenNoBusinessesExist() {

        // Mock tom liste
        when(businessRepository.findAll())
                .thenReturn(List.of());

        // Kalder service metode
        List<Business> businesses = businessService.getAllBusinesses();

        // Verificerer resultat
        assertTrue(businesses.isEmpty());

        // Verificerer at repository blev kaldt
        verify(businessRepository).findAll();
    }
}