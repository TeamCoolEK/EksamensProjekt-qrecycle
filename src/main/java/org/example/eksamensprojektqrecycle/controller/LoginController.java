package org.example.eksamensprojektqrecycle.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.eksamensprojektqrecycle.model.dto.LoginDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*") // Browseren afviser enhver api kald fra frontend, med mindre origins er sat til * (alle) eller localhost:(Frontends port) ELLER teamcool.swag.dk:(port) :)
public class LoginController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

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
}
