package org.example.eksamensprojektqrecycle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception { // Spring filter chain config
        http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/").authenticated()
                    .requestMatchers("/login").permitAll()
                        .requestMatchers("/driver/**").permitAll() // Midlertidigt til test indtil login er helt klar
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()); //CSRF disable for REST API's
        return http.build();
    }
}
