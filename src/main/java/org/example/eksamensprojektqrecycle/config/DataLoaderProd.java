package org.example.eksamensprojektqrecycle.config;

import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.EndStop;
import org.example.eksamensprojektqrecycle.model.enums.Role;
import org.example.eksamensprojektqrecycle.repository.EndStopRepository;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class DataLoaderProd implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EndStopRepository endStopRepository;

    public DataLoaderProd(PasswordEncoder passwordEncoder,
                          UserRepository userRepository,
                          EndStopRepository endStopRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.endStopRepository = endStopRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) { // hvis userRepository er over 0, så skal den ikke køre commandLineRunner
            return;
        }
        //Admin
        AppUser admin = userRepository.save(new AppUser("admin", passwordEncoder.encode("admin"), Role.ADMIN));

        //EndStop
        endStopRepository.save(new EndStop(1, "Retortvej 38, 2500 Valby"));

        System.out.println("ADMIN og ENDSTOP Loaded. Skift Adgangskode til ADMIN i Bruger Adminstrationen!");
    }
}
