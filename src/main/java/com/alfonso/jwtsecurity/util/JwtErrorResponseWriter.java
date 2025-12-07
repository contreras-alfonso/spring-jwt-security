package com.alfonso.jwtsecurity.util;

import com.alfonso.jwtsecurity.entity.Error;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JwtErrorResponseWriter {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void sendUnauthorized(HttpServletResponse response, String message, String errorMsg) throws IOException {
        sendError(response, HttpServletResponse.SC_UNAUTHORIZED, message, errorMsg);
    }

    public static void sendError(HttpServletResponse response, int status, String message, String errorMsg) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        Error error = new Error();
        error.setError(errorMsg);
        error.setMsg(message);
        error.setStatus(status);

        response.getWriter().write(mapper.writeValueAsString(error));
    }
}