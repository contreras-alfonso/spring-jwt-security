package com.alfonso.jwtsecurity.exception;

import com.alfonso.jwtsecurity.util.JwtErrorResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        JwtErrorResponseWriter.sendError(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "No tienes permisos para realizar esta acción",
                accessDeniedException.getMessage()
        );
    }
}