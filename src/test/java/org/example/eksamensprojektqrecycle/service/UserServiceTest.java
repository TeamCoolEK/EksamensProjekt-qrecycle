package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.CreateUserDTO;
import org.example.eksamensprojektqrecycle.model.dto.UserResponseDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.enums.Role;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    // Mock repository så vi ikke rammer rigtig database
    private final UserRepository userRepository =
            mock(UserRepository.class);

    // Mock password encoder
    private final PasswordEncoder passwordEncoder =
            mock(PasswordEncoder.class);

    // Service som testes
    private final UserService userService =
            new UserService(userRepository, passwordEncoder);


    //QE-330: Test at alle bruger vises korrekt med navn og rolle fra DB
    @Test
    void getAllUsers_shouldReturnAllUsersWithCorrectUsernameAndRole() {
        // Arrange: Opret mock brugere som "kommer fra databasen"
        AppUser admin = new AppUser();
        admin.setId(1);
        admin.setUsername("admin");
        admin.setPassword("encoded-admin-password");
        admin.setRole(Role.ADMIN);

        AppUser driver = new AppUser();
        driver.setId(2);
        driver.setUsername("driver1");
        driver.setPassword("encoded-driver-password");
        driver.setRole(Role.DRIVER);

        AppUser business = new AppUser();
        business.setId(3);
        business.setUsername("business1");
        business.setPassword("encoded-business-password");
        business.setRole(Role.BUSINESS);

        // Mock repository til at returnere disse brugere
        when(userRepository.findAll())
                .thenReturn(Arrays.asList(admin, driver, business));

        // Act: Kald metoden der skal testes
        List<UserResponseDTO> result = userService.getAllUsers();

        // Assert: Verificer at alle brugere returneres
        assertNotNull(result, "Result should not be null");
        assertEquals(3, result.size(), "Should return 3 users");

        // Verificer første bruger (admin)
        UserResponseDTO adminDTO = result.get(0);
        assertEquals(1, adminDTO.getId(), "Admin ID should match");
        assertEquals("admin", adminDTO.getUsername(), "Admin username should match");
        assertEquals("ADMIN", adminDTO.getRole(), "Admin role should be ADMIN");

        // Verificer anden bruger (driver)
        UserResponseDTO driverDTO = result.get(1);
        assertEquals(2, driverDTO.getId(), "Driver ID should match");
        assertEquals("driver1", driverDTO.getUsername(), "Driver username should match");
        assertEquals("DRIVER", driverDTO.getRole(), "Driver role should be DRIVER");

        // Verificer tredje bruger (business)
        UserResponseDTO businessDTO = result.get(2);
        assertEquals(3, businessDTO.getId(), "Business ID should match");
        assertEquals("business1", businessDTO.getUsername(), "Business username should match");
        assertEquals("BUSINESS", businessDTO.getRole(), "Business role should be BUSINESS");

        // Verificer at repository blev kaldt korrekt
        verify(userRepository).findAll();
    }

    // Tester at chauffør gemmes korrekt når pinkoden er præcis 4 cifre
    @Test
    void createUser_shouldSaveDriver_whenPinIsFourDigits() {

        // Opretter test DTO
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("driver1");
        dto.setPassword("Abcd1234!");
        dto.setRole(Role.DRIVER);

        // Mock encoder
        when(passwordEncoder.encode("Abcd1234!"))
                .thenReturn("Encoded-driver-password");

        // Kalder service
        userService.createUser(dto);

        // Fanger objektet som sendes til repository
        ArgumentCaptor<AppUser> captor =
                ArgumentCaptor.forClass(AppUser.class);

        verify(userRepository).save(captor.capture());

        // Henter gemt bruger
        AppUser savedUser = captor.getValue();

        // Tjekker data
        assertEquals("driver1", savedUser.getUsername());
        assertEquals("Encoded-driver-password", savedUser.getPassword());
        assertEquals(Role.DRIVER, savedUser.getRole());

        // Verificerer encoding
        verify(passwordEncoder).encode("Abcd1234!");
    }


    // Tester at chauffør fejler hvis pinkode ikke er 4 cifre
    @Test
    void createUser_shouldThrowException_whenDriverPinIsNotFourDigits() {

        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("driver1");
        dto.setPassword("12345");
        dto.setRole(Role.DRIVER);

        // Forventer exception
        assertThrows(RuntimeException.class, () -> {
            userService.createUser(dto);
        });

        // Tjekker at intet gemmes
        verify(userRepository, never()).save(any());
    }


    // Tester at admin gemmes korrekt når password har stort bogstav og tal
    @Test
    void createUser_shouldSaveAdmin_whenPasswordHasUppercaseAndNumberAndLowercaseAndSpecialCharacters() {

        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("admin1");
        dto.setPassword("Admin1!");
        dto.setRole(Role.ADMIN);

        // Mock encoder
        when(passwordEncoder.encode("Admin1!"))
                .thenReturn("encoded-admin-password");

        userService.createUser(dto);

        ArgumentCaptor<AppUser> captor =
                ArgumentCaptor.forClass(AppUser.class);

        verify(userRepository).save(captor.capture());

        AppUser savedUser = captor.getValue();

        assertEquals("admin1", savedUser.getUsername());
        assertEquals("encoded-admin-password", savedUser.getPassword());
        assertEquals(Role.ADMIN, savedUser.getRole());

        // Verificerer encoding
        verify(passwordEncoder).encode("Admin1!");
    }


    // Tester at admin password fejler hvis der ikke er stort bogstav
    @Test
    void createUser_shouldThrowException_whenAdminPasswordHasNoUppercase() {

        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("admin1");
        dto.setPassword("admin1");
        dto.setRole(Role.ADMIN);

        assertThrows(RuntimeException.class, () -> {
            userService.createUser(dto);
        });

        verify(userRepository, never()).save(any());
    }

    // Tester at admin password fejler hvis der ikke er tal
    @Test
    void createUser_shouldThrowException_whenAdminPasswordHasNoNumber() {

        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("admin1");
        dto.setPassword("Admin");
        dto.setRole(Role.ADMIN);

        assertThrows(RuntimeException.class, () -> {
            userService.createUser(dto);
        });

        verify(userRepository, never()).save(any());
    }

    // Tester at admin password fejler hvis der ikke er specialtegn
    @Test
    void createUser_shouldThrowException_whenAdminPasswordHasNoSpecialCharacters() {

        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("admin1");
        dto.setPassword("Admin1");
        dto.setRole(Role.ADMIN);

        assertThrows(RuntimeException.class, () -> {
            userService.createUser(dto);
        });

        verify(userRepository, never()).save(any());
    }

    // Tester at username er påkrævet
    @Test
    void createUser_shouldThrowException_whenUsernameIsEmpty() {

        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("");
        dto.setPassword("1234");
        dto.setRole(Role.DRIVER);

        assertThrows(RuntimeException.class, () -> {
            userService.createUser(dto);
        });

        verify(userRepository, never()).save(any());
    }

    // Tester at password er påkrævet
    @Test
    void createUser_shouldThrowException_whenPasswordIsEmpty() {

        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("driver1");
        dto.setPassword("");
        dto.setRole(Role.DRIVER);

        assertThrows(RuntimeException.class, () -> {
            userService.createUser(dto);
        });

        verify(userRepository, never()).save(any());
    }


    // Tester at rolle er påkrævet
    @Test
    void createUser_shouldThrowException_whenRoleIsMissing() {

        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("user1");
        dto.setPassword("1234");
        dto.setRole(null);

        assertThrows(RuntimeException.class, () -> {
            userService.createUser(dto);
        });

        verify(userRepository, never()).save(any());
    }
}