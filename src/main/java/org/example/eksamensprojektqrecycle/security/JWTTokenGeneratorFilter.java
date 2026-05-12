package org.example.eksamensprojektqrecycle.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

//Lader spring håndtere dependency injection
@Component
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {
    //Henter JWT Key ud fra properties profil
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    //FÅ STYR PÅ DET HER ANOEP
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("JWTTokenGeneratiorFilter");
        if (authentication != null) {
            var key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            String token = String.valueOf(Jwts.builder()
                    .issuer("QRecycle")
                    .subject("JWT Token")
                    .claim("username", authentication.getName())
                    .claim("authorities", populateAuthorities(authentication.getAuthorities()))
                    .issuedAt(new Date())
                    .expiration(new Date(new Date().getTime() + 30000000))
                    .signWith(key).compact());
            response.setHeader("Authorization", token);
            System.out.println(token);
        }
        filterChain.doFilter(request, response);
    }

    //Sender kun JWT Token ved /doLogin
    @Override
    public boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/doLogin");
    }

    //Metode til at populate authorities i doFilterInternal
    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}
