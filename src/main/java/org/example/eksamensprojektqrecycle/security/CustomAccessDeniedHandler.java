package org.example.eksamensprojektqrecycle.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LocalDateTime currentTime = LocalDateTime.now();
        String path = request.getRequestURI();
        response.setHeader("QResycle-error-reason", "Authentication failed");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        //Json fejl besked auth response ved forkert rolle
        String jsonResponse =
                String.format("{\"timestamp\":\"%s\", \"status\":\"%d\", \"error\":\"%s\", \"path\":\"%s\"}",
                        currentTime, HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), path);
        response.getWriter().write(jsonResponse);
    }


}
