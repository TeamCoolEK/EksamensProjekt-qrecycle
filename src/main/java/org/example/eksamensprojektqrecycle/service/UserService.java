package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.CreateUserDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.enums.Role;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // Repository bruges til databasekald
    private final UserRepository userRepository;

    // Constructor injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Opretter ny bruger
    public AppUser createUser(CreateUserDTO dto) {

        // Validerer username
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new RuntimeException("Username mangler");
        }

        // Validerer password
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("Password mangler");
        }

        // Validerer rolle
        if (dto.getRole() == null) {
            throw new RuntimeException("Rolle mangler");
        }

        // Validerer password regler afhængigt af rolle
        validatePassword(dto);

        // Opretter nyt AppUser objekt
        AppUser user = new AppUser();

        // Mapper DTO data over på entity
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());

        // Gemmer brugeren i databasen
        return userRepository.save(user);
    }

    // Validerer password regler for forskellige roller
    private void validatePassword(CreateUserDTO dto) {

        String password = dto.getPassword();

        // Chauffør skal have 4 cifret pinkode
        if (dto.getRole() == Role.DRIVER) {
            if (!password.matches("\\d{4}")) {
                throw new RuntimeException(
                        "Chauffør skal have 4-cifret pinkode"
                );
            }
        }

        // Admin skal have sikkert password
        // Password skal indeholde stort bogstav og tal
        if (dto.getRole() == Role.ADMIN) {
            boolean hasUppercase =
                    password.matches(".*[A-Z].*");
            boolean hasNumber =
                    password.matches(".*\\d.*");
            if (!hasUppercase || !hasNumber) {
                throw new RuntimeException(
                        "Admin password skal indeholde stort bogstav og tal"
                );
            }
        }
    }
}