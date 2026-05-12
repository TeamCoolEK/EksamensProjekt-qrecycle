package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.CreateUserDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.enums.Role;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    // Constructor injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Opretter ny bruger
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

        validatePassword(dto);

        AppUser user = new AppUser();

        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());

        return userRepository.save(user);
    }

    // Bruges af Spring Security til login
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

    // Validerer password regler for forskellige roller
    private void validatePassword(CreateUserDTO dto) {

        String password = dto.getPassword();

        if (dto.getRole() == Role.DRIVER) {
            if (!password.matches("\\d{4}")) {
                throw new RuntimeException("Chauffør skal have 4-cifret pinkode");
            }
        }

        if (dto.getRole() == Role.ADMIN) {
            boolean hasUppercase = password.matches(".*[A-Z].*");
            boolean hasNumber = password.matches(".*\\d.*");

            if (!hasUppercase || !hasNumber) {
                throw new RuntimeException("Admin password skal indeholde stort bogstav og tal");
            }
        }
    }
}