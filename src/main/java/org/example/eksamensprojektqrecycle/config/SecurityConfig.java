package org.example.eksamensprojektqrecycle.config;

import org.example.eksamensprojektqrecycle.exceptionHandling.CustomAccessDeniedHandler;
import org.example.eksamensprojektqrecycle.exceptionHandling.CustomBasicAuthenticationEntryPoint;
import org.example.eksamensprojektqrecycle.security.JWTTokenGeneratorFilter;
import org.example.eksamensprojektqrecycle.security.JWTTokenValidatorFilter;
import org.example.eksamensprojektqrecycle.security.RequestValidationBeforeFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
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

    private final JWTTokenGeneratorFilter jwtTokenGeneratorFilter;
    private final JWTTokenValidatorFilter jwtTokenValidatorFilter;
    //Dependency injecter, for at læse JWT Key value
    public SecurityConfig(JWTTokenGeneratorFilter jwtTokenGeneratorFilter, JWTTokenValidatorFilter jwtTokenValidatorFilter) {
        this.jwtTokenGeneratorFilter = jwtTokenGeneratorFilter;
        this.jwtTokenValidatorFilter = jwtTokenValidatorFilter;
    }

    // spring filter chain config
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .addFilterAfter(jwtTokenGeneratorFilter, BasicAuthenticationFilter.class) //Generere Token
                .addFilterBefore(jwtTokenValidatorFilter, BasicAuthenticationFilter.class) //Validere Token
                .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable()) //CSRF disable for REST API's
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) //Køre igennem Cors Config
                .authorizeHttpRequests(auth -> auth
                        //Bruger hasAuthority, da hasRole kræver ROLE_ prefix i starten af rollen som er gemt i DB
                        .requestMatchers("/admin/**").hasAuthority("ADMIN") //alle Admin endpoints kan kun tilgåes af admin rollen
                        .requestMatchers("/driver/**").hasAnyAuthority("DRIVER", "ADMIN") //-..-
                        .requestMatchers("/business/**").hasAuthority("BUSINESS") //-..-
                        .requestMatchers("/login", "/register", "/", "/jwtkey", "/auth", "/doLogin").permitAll() //Endpoints som er tilladt uden login
                        .anyRequest().authenticated()
                )
                //Fjerner JSESSIONID, så man skal logge ind per request (til JWT token)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                //sætter egen authEntryPoint, til at returnere custom header ved forkert login (401, unauthorized)
                .httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()))
                //sætter egen accessDeniedHandler til at returnere custom header ved forkert rolle (403, forbidden)
                //.accessDeniedPage("/denied") redirects til en custom side vi laver
                .exceptionHandling(ehc -> ehc.accessDeniedHandler(
                        new CustomAccessDeniedHandler())); //GLOBAL CONFIG
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
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    // password encoder returnere bcrypt encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
