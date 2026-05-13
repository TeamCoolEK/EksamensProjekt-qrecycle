package org.example.eksamensprojektqrecycle.controller;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.eksamensprojektqrecycle.model.dto.LoginDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Base64;

@RestController
public class LoginController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/register") // registerer en ny user
    public ResponseEntity<String> registerUser(@RequestBody AppUser appUser) {
        AppUser savedUser;
        ResponseEntity<String> response = null;
        try {
            String hashedPassword = passwordEncoder.encode(appUser.getPassword()); // henter user password og encoder det.
            appUser.setPassword(hashedPassword); // gemmer encoded password som user password
            savedUser = userRepository.save(appUser); // gemmer appUser i databasen og lokal variabel savedUser
            if(savedUser.getId() > 0) { // hvis saved user id er større end 0 retuneres HTTPStatus CREATED
                response = ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
            }
        } catch (Exception e) {
            response = ResponseEntity.status( // ellers retuneres 500
                    HttpStatus.INTERNAL_SERVER_ERROR).body("Exception occured due to " + e.getMessage()
            );
        }
        return response; // returnere respons
    }

    @PostMapping("/doLogin")
    public ResponseEntity<String> doLogin(@RequestBody AppUser appUser) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.OK).body("User logged in successfully");
        } else {
            throw new UsernameNotFoundException("User not found: " + appUser.getUsername());
        }
    }
    //get til at hente user fra spring context
    @GetMapping("/auth")
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    //Retunere JWT key på nuværende bruger
    @GetMapping("/jwtkey")
    public String getJwtKey() {
        SecretKey key = Jwts.SIG.HS256.key().build();
        String secretString = Base64.getEncoder().encodeToString(key.getEncoded());
        return secretString;
    }

    @GetMapping("/admin")
    public String getAdminUser() {
        return "admin";
    }
}
