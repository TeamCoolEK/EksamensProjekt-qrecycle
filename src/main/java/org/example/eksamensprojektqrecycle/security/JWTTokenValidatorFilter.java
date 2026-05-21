package org.example.eksamensprojektqrecycle.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

//Lader spring håndtere dependency injection
@Component
public class JWTTokenValidatorFilter extends OncePerRequestFilter {
    //Henter JWT Key ud fra properties profil
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = request.getHeader("Authorization");
        System.out.println("JWT Validator header called: " + jwtToken);
        if (jwtToken != null) {
            try {
                SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(jwtToken)
                        .getPayload();
                String username = String.valueOf(claims.get("username"));
                String authorities = claims.get("authorities").toString();
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                System.out.println("JWT parse failed: " + e.getMessage());
                throw new BadCredentialsException("Invalid JWT Token");
            }
        }
        filterChain.doFilter(request, response);
    }

    //Validator skipper på doLogin post
//    @Override
//    public boolean shouldNotFilter(HttpServletRequest request) {
//        return request.getServletPath().equals("/login");
//    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.equals("/login")
                || path.equals("/api/login")
                || path.equals("/register")
                || path.equals("/api/register")
                || path.equals("/error")
                || path.equals("/api/error")
                || path.equals("/jwtkey")
                || path.equals("/api/jwtkey")
                || request.getMethod().equals("OPTIONS");
    }
}
