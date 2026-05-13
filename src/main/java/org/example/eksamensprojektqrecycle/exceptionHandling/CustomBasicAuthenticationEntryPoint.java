package org.example.eksamensprojektqrecycle.exceptionHandling;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {


    //Custom entryPoint ved forkert login (401)
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        LocalDateTime currentTime = LocalDateTime.now();
        String path = request.getRequestURI();
        //response.setHeader("QResycle-error-reason", "Authentication failed"); // custom header
        response.setHeader("WWW-Authenticate", "Basic realm=\"MyApp\""); // pop up login vindue
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        //Json fejl besked auth response ved forkert login
        String jsonResponse =
                String.format("{\"timestamp\":\"%s\", \"status\":\"%d\", \"error\":\"%s\", \"path\":\"%s\"}",
                        currentTime, HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), path);
        response.getWriter().write(jsonResponse);
    }
}
