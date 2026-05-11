package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.entity.AppUser;
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

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    // Brokker sig over null pointer exception, men bruger enum som ikke kan returnere null, så vi ignorere
    public UserDetails loadUserByUsername (String username) throws UsernameNotFoundException {
        String password;
        String userName;
        List<GrantedAuthority> authorities;
        Optional<AppUser> appUser = userRepository.findByUsername(username); // Bruger Optional til at undgå null pointer (da en user måske ikke eksistere i DB, vil vi gerne undgå null pointer, da det crasher systemet, og i stedet vil vi gerne throw userNameNotFoundException)
        if (appUser.isPresent()) { // hvis app user findes, gør dette.
            userName = appUser.get().getUsername(); // Gemmer appUser username
            password = appUser.get().getPassword(); // Gemmer appUser password
            authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(appUser.get().getRole().name())); // .name() kornventer role Enum til String og giver appUser autoritet
        } else {
            throw new UsernameNotFoundException(username + "Username not found");
        }
        return new User(userName, password, authorities);
    }
}