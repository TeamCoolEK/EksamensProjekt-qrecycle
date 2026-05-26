package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.BusinessResponseDTO;
import org.example.eksamensprojektqrecycle.model.dto.CreateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.dto.UpdateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.model.enums.Role;
import org.example.eksamensprojektqrecycle.repository.BusinessRepository;
import org.example.eksamensprojektqrecycle.repository.CollectionRepository;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusinessServiceTest {

    // Mock repository til virksomhed
    private final BusinessRepository businessRepository = mock(BusinessRepository.class);

    // Mock repository til brugere
    private final UserRepository userRepository = mock(UserRepository.class);

    private final CollectionRepository collectionRepository = mock(CollectionRepository.class);

    // Mock passwordEncoder
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    // Service som testes
    private final BusinessService businessService =
            new BusinessService(businessRepository, userRepository, collectionRepository, passwordEncoder);

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
        dto.setPassword("Franks!1");

        // Mock save på user repository
        when(userRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Mock save på business repository
        when(businessRepository.save(any(Business.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(passwordEncoder.encode(anyString()))
                .thenReturn("Encoded-password!1");

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
        assertEquals("Encoded-password!1", savedUser.getPassword());

        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordEncoder).encode(passwordCaptor.capture());
        assertEquals("Franks!1", passwordCaptor.getValue());
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
        List<BusinessResponseDTO> businesses = businessService.getAllBusinesses();

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
        List<BusinessResponseDTO> businesses = businessService.getAllBusinesses();

        // Verificerer resultat
        assertTrue(businesses.isEmpty());

        // Verificerer at repository blev kaldt
        verify(businessRepository).findAll();
    }

    // Tester at virksomhed kan opdateres når data er valid
    @Test
    void updateBusiness_shouldUpdateBusiness_whenDataIsValid() {

        // Opretter eksisterende virksomhed
        Business business = new Business();
        business.setId(1);
        business.setCompanyName("Franks Pizza");
        business.setContactPerson("Frank");
        business.setPhoneNumber("28123456");
        business.setAddress("Nørrebrogade 12");

        // Opretter DTO med nye oplysninger
        UpdateBusinessDTO dto = new UpdateBusinessDTO();
        dto.setCompanyName("Franks Pizza Updated");
        dto.setContactPerson("Frank Hansen");
        dto.setPhoneNumber("30112233");
        dto.setAddress("Amagerbrogade 45");

        // Mock findById
        when(businessRepository.findById(1))
                .thenReturn(Optional.of(business));

        // Mock save
        when(businessRepository.save(any(Business.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Kalder service metode
        BusinessResponseDTO updatedBusiness = businessService.updateBusiness(1, dto);

        // Verificerer opdaterede oplysninger
        assertEquals("Franks Pizza Updated", updatedBusiness.getCompanyName());
        assertEquals("Frank Hansen", updatedBusiness.getContactPerson());
        assertEquals("30112233", updatedBusiness.getPhoneNumber());
        assertEquals("Amagerbrogade 45", updatedBusiness.getAddress());

        // Verificerer repository kald
        verify(businessRepository).findById(1);
        verify(businessRepository).save(business);
    }

    // Tester fejl hvis virksomhed ikke findes
    @Test
    void updateBusiness_shouldThrowException_whenBusinessDoesNotExist() {

        // Opretter DTO
        UpdateBusinessDTO dto = new UpdateBusinessDTO();
        dto.setCompanyName("Franks Pizza Updated");
        dto.setContactPerson("Frank Hansen");
        dto.setPhoneNumber("30112233");
        dto.setAddress("Amagerbrogade 45");

        // Mock findById til tom Optional
        when(businessRepository.findById(1))
                .thenReturn(Optional.empty());

        // Forventer exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            businessService.updateBusiness(1, dto);
        });

        // Verificerer fejlbesked
        assertEquals("Virksomhed blev ikke fundet", exception.getMessage());

        // Verificerer at save ikke bliver kaldt
        verify(businessRepository, never()).save(any());
    }

    // Tester fejl hvis virksomhedsnavn mangler
    @Test
    void updateBusiness_shouldThrowException_whenCompanyNameIsEmpty() {

        // Opretter eksisterende virksomhed
        Business business = new Business();
        business.setId(1);

        // Opretter DTO med tomt virksomhedsnavn
        UpdateBusinessDTO dto = new UpdateBusinessDTO();
        dto.setCompanyName("");
        dto.setContactPerson("Frank Hansen");
        dto.setPhoneNumber("30112233");
        dto.setAddress("Amagerbrogade 45");

        // Mock findById
        when(businessRepository.findById(1))
                .thenReturn(Optional.of(business));

        // Forventer exception
        assertThrows(RuntimeException.class, () -> {
            businessService.updateBusiness(1, dto);
        });

        // Verificerer at save ikke bliver kaldt
        verify(businessRepository, never()).save(any());
    }

    // Tester fejl hvis kontaktperson mangler
    @Test
    void updateBusiness_shouldThrowException_whenContactPersonIsEmpty() {

        // Opretter eksisterende virksomhed
        Business business = new Business();
        business.setId(1);

        // Opretter DTO med tom kontaktperson
        UpdateBusinessDTO dto = new UpdateBusinessDTO();
        dto.setCompanyName("Franks Pizza Updated");
        dto.setContactPerson("");
        dto.setPhoneNumber("30112233");
        dto.setAddress("Amagerbrogade 45");

        // Mock findById
        when(businessRepository.findById(1))
                .thenReturn(Optional.of(business));

        // Forventer exception
        assertThrows(RuntimeException.class, () -> {
            businessService.updateBusiness(1, dto);
        });

        // Verificerer at save ikke bliver kaldt
        verify(businessRepository, never()).save(any());
    }

    // Tester fejl hvis telefonnummer mangler
    @Test
    void updateBusiness_shouldThrowException_whenPhoneNumberIsEmpty() {

        // Opretter eksisterende virksomhed
        Business business = new Business();
        business.setId(1);

        // Opretter DTO med tomt telefonnummer
        UpdateBusinessDTO dto = new UpdateBusinessDTO();
        dto.setCompanyName("Franks Pizza Updated");
        dto.setContactPerson("Frank Hansen");
        dto.setPhoneNumber("");
        dto.setAddress("Amagerbrogade 45");

        // Mock findById
        when(businessRepository.findById(1))
                .thenReturn(Optional.of(business));

        // Forventer exception
        assertThrows(RuntimeException.class, () -> {
            businessService.updateBusiness(1, dto);
        });

        // Verificerer at save ikke bliver kaldt
        verify(businessRepository, never()).save(any());
    }

    // Tester fejl hvis adresse mangler
    @Test
    void updateBusiness_shouldThrowException_whenAddressIsEmpty() {

        // Opretter eksisterende virksomhed
        Business business = new Business();
        business.setId(1);

        // Opretter DTO med tom adresse
        UpdateBusinessDTO dto = new UpdateBusinessDTO();
        dto.setCompanyName("Franks Pizza Updated");
        dto.setContactPerson("Frank Hansen");
        dto.setPhoneNumber("30112233");
        dto.setAddress("");

        // Mock findById
        when(businessRepository.findById(1))
                .thenReturn(Optional.of(business));

        // Forventer exception
        assertThrows(RuntimeException.class, () -> {
            businessService.updateBusiness(1, dto);
        });

        // Verificerer at save ikke bliver kaldt
        verify(businessRepository, never()).save(any());
    }
    // Tester at virksomhed slettes når den findes
    @Test
    void deleteBusiness_shouldDeleteBusiness_whenBusinessExists() {

        // Opretter test virksomhed
        Business business = new Business();
        business.setId(1);
        business.setCompanyName("Franks Pizza");

        // Mock findById
        when(businessRepository.findById(1))
                .thenReturn(Optional.of(business));

        // Kalder service metode
        businessService.deleteBusiness(1);

        // Verificerer at virksomheden findes
        verify(businessRepository).findById(1);

        // Verificerer at virksomheden slettes
        verify(businessRepository).delete(business);
    }

    // Tester fejl hvis virksomhed ikke findes
    @Test
    void deleteBusiness_shouldThrowException_whenBusinessDoesNotExist() {

        // Mock findById til tom Optional
        when(businessRepository.findById(1))
                .thenReturn(Optional.empty());

        // Forventer exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            businessService.deleteBusiness(1);
        });

        // Verificerer fejlbesked
        assertEquals("Virksomheden blev ikke fundet", exception.getMessage());

        // Verificerer at delete ikke kaldes
        verify(businessRepository, never()).delete(any(Business.class));
    }
}