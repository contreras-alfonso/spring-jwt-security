package com.alfonso.jwtsecurity.filter;

import com.alfonso.jwtsecurity.exception.InvalidJwtTokenException;
import com.alfonso.jwtsecurity.exception.MissingAuthorizationHeaderException;
import com.alfonso.jwtsecurity.service.JwtService;
import com.alfonso.jwtsecurity.util.JwtErrorResponseWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;
import com.alfonso.jwtsecurity.entity.Error;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {

            String path = request.getServletPath();

            if (path.startsWith("/api/auth/")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new MissingAuthorizationHeaderException("Authorization header missing or malformed");
            }

            String jwt = getJwtFormRequest(request);

            if (!jwtService.isValidToken(jwt)) {
                throw new InvalidJwtTokenException("Invalid token");
            }

            String username = jwtService.extractUsernameFromToken(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.validateTokenForUser(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (MissingAuthorizationHeaderException ex) {
            JwtErrorResponseWriter.sendUnauthorized(response, "Token no proveído", ex.getMessage());
        } catch (InvalidJwtTokenException ex) {
            JwtErrorResponseWriter.sendUnauthorized(response, "Token inválido", ex.getMessage());
        }
    }


    private String getJwtFormRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        //Bearer <token>
        return authHeader.substring(7);
    }
}
