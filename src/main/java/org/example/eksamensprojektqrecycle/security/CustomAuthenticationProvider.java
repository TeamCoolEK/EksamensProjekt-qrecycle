package org.example.eksamensprojektqrecycle.security;

import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class CustomAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //overrider authentication metoden, til at authenticate en bruger
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = Objects.requireNonNull(authentication.getCredentials()).toString(); //Tjekker om objektet ikke er null, da toString kan invoke nullPointerException som kan crash systemet
        System.out.println("Load user kaldt: username: " + username);
        Optional<AppUser> appUser = Optional.empty();
        try {
            appUser = userRepository.findByUsername(username);
        } catch (Exception e) {
            System.out.println("Database fejl: " + e.getMessage());
        }
        if (appUser.isPresent()) {
            if (passwordEncoder.matches(password, appUser.get().getPassword())) {
                List<GrantedAuthority> authorities = new ArrayList<>(); //tilføjer user til auth list
                authorities.add(new SimpleGrantedAuthority(appUser.get().getRole().name())); //name formatere enum til string
                return new UsernamePasswordAuthenticationToken(username, password, authorities);
            } else {
                throw new BadCredentialsException("Bad credentials"); //forkert password
            }
        } else {
            throw new BadCredentialsException("No user found with username: " + username); //forkert username
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
