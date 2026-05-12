package org.example.eksamensprojektqrecycle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    //Tilladte cors endpoints, til prod og dev (ændres til teamcool.swag.dk, i deploy sekvensen aka når der merges med main)
    @Value("${app.cors.allowed-origins:http://localhost:63342}") // app.cors.... henter fra dev og prod properties... localhost:63342 er en fallback, hvis de skulle fejle.
    private String allowedOrigins; // origins endpoints gemt her.

    // spring filter chain config
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //CSRF disable for REST API's
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) //Køre igennem Cors Config
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/admin").authenticated()
                    .requestMatchers("/login", "/register", "/").permitAll() //Endpoints som er tilladt uden login
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
    // cors configuration: Sætter cors op i spring security, så man ikke behøver at sætte det i controlleren med fx CROSS = *
    // FÅ STYR PÅ INDEN EKSAMEN
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins.split(","))); //origins gemmes i en liste ud fra allowedOrigins String som separeres med ,
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); //Tillader disse endpoint metoder
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); //credentials til spring security

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    // password encoder returnere bcrypt encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
