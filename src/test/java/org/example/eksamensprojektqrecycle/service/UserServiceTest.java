package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.CreateUserDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.enums.Role;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    // Mock repository så vi ikke rammer rigtig database
    private final UserRepository userRepository =
            mock(UserRepository.class);

    // Service som testes
    private final UserService userService =
            new UserService(userRepository);


    // Tester at chauffør gemmes korrekt når pinkoden er præcis 4 cifre
    @Test
    void createUser_shouldSaveDriver_whenPinIsFourDigits() {

        // Opretter test DTO
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("driver1");
        dto.setPassword("1234");
        dto.setRole(Role.DRIVER);

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
        assertEquals("1234", savedUser.getPassword());
        assertEquals(Role.DRIVER, savedUser.getRole());
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

        userService.createUser(dto);

        ArgumentCaptor<AppUser> captor =
                ArgumentCaptor.forClass(AppUser.class);

        verify(userRepository).save(captor.capture());

        AppUser savedUser = captor.getValue();

        assertEquals("admin1", savedUser.getUsername());
        assertEquals("Admin1!", savedUser.getPassword());
        assertEquals(Role.ADMIN, savedUser.getRole());
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