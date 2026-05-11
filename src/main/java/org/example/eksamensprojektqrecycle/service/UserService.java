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

    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername (String username) throws UsernameNotFoundException {
        String password;
        List<GrantedAuthority> authorities;
        Optional<AppUser> appUser = userRepository.findByUsername(username); // Bruger Optional til at undgå null pointer (da en user måske ikke eksistere i DB, vil vi gerne undgå null pointer, da det crasher systemet, og i stedet vil vi gerne throw userNameNotFoundException)
        if (appUser.isPresent()) {
            username = appUser.get().getUsername();
            password = appUser.get().getPassword();
            authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(appUser.get().getRole().name())); // .name() kornventer role Enum til String
        } else {
            throw new UsernameNotFoundException(username + "Username not found");
        }
        return new User(username, password, authorities);
    }
}