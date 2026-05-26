package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.CreateUserDTO;
import org.example.eksamensprojektqrecycle.model.dto.UpdateUserDTO;
import org.example.eksamensprojektqrecycle.model.dto.UserResponseDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.enums.Role;
import org.example.eksamensprojektqrecycle.repository.BusinessRepository;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BusinessRepository businessRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.businessRepository = businessRepository;
    }

    public AppUser createUser(CreateUserDTO dto) {

        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new RuntimeException("Username mangler");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("Password mangler");
        }

        if (dto.getRole() == null) {
            throw new RuntimeException("Rolle mangler");
        }

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Brugernavn er allerede i brug");
        }

        validatePassword(dto);

        AppUser user = new AppUser();

        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        return userRepository.save(user);
    }

    //QE-206: Implementer databasekald til sletning
    public void deleteUser(int id) {
        //Find brugeren i DB
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bruger blev ikke fundet"));

        //Forbyd sletning af egen bruger(admin)
        String currentUsername = getCurrentUsername();
        if (currentUsername != null && user.getUsername().equals(currentUsername)) {
            throw new RuntimeException("Du kan ikke slette din egen bruger");
        }
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin brugere kan ikke slettes af sikkerhedsmæssige årsager");
        }
        //QE-206: Slet brugeren fra DB (SQL: DELETE FROM app_user WHERE id = ?
        businessRepository.findByAppUser(user)
                .ifPresent(businessRepository::delete);

        userRepository.delete(user);
    }

    //Helper: Hent nuværende brugers username fra security context
    private String getCurrentUsername() {

        try {
            Object principal = SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                return userDetails.getUsername();
            }

            return principal.toString();

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<AppUser> appUser = userRepository.findByUsername(username);

        if (appUser.isEmpty()) {
            throw new UsernameNotFoundException(username + " username not found");
        }

        AppUser user = appUser.get();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));

        return new User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    private void validatePassword(CreateUserDTO dto) {

        String password = dto.getPassword();

        if (password.length() < 4) {
            throw new RuntimeException("Password skal være mindst 4 tegn");
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasSpecialCharacter = password.matches(".*[^a-zA-Z0-9].*");

        if (!hasUppercase || !hasNumber || !hasLowercase || !hasSpecialCharacter) {
            throw new RuntimeException("Password skal indeholde stort bogstav, lille bogstav, tal og specialtegn");
        }
    }

    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toUserResponseDto)
                .collect(Collectors.toList());
    }

    private UserResponseDTO toUserResponseDto(AppUser user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);

        return dto;
    }

    public void updateUser(int id, UpdateUserDTO dto) {

        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bruger blev ikke fundet"));

        user.setUsername(dto.getUsername());

        String encryptedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(encryptedPassword);

        userRepository.save(user);
    }
}