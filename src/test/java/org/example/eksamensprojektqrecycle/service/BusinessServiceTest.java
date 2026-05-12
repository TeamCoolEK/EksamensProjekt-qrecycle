package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.CreateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.model.enums.Role;
import org.example.eksamensprojektqrecycle.repository.BusinessRepository;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
}