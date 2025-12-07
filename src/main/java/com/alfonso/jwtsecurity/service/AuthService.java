package com.alfonso.jwtsecurity.service;

import com.alfonso.jwtsecurity.dto.*;
import com.alfonso.jwtsecurity.entity.User;
import com.alfonso.jwtsecurity.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Transactional
    public void register(RegisterRequest registerRequest) {
        if (this.userRepository.existsByUsername(registerRequest.getUsername())) {
            //TODO: Crear exepción personalizada
            throw new IllegalArgumentException("El usuario ya existe");
        }

        User user = User.builder().username(registerRequest.getUsername()).password(passwordEncoder.encode(registerRequest.getPassword())).fullname(registerRequest.getFullname()).role(registerRequest.getRole()).build();

        this.userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Generate Token Pair

        String username = jwtService.extractUsername(authentication);
        List<String> roles = jwtService.extractRoles(authentication);
        TokenPair tokenPair = jwtService.generateTokenPair(authentication);

        return new LoginResponse(username, roles, tokenPair);
    }

    public TokenPair refreshToken(@Valid RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        //check if it is valid refresh token
        if (!jwtService.isRefreshToken(refreshToken)) {
            // TODO: Agregar validación personalizada
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String user = jwtService.extractUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user);

        if (userDetails == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String accessToken = jwtService.generateAccessToken(authentication);
        return new TokenPair(accessToken, refreshToken);
    }
}
