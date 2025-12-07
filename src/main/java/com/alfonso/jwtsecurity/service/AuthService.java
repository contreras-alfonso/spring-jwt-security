package com.alfonso.jwtsecurity.service;

import com.alfonso.jwtsecurity.dto.*;
import com.alfonso.jwtsecurity.entity.User;
import com.alfonso.jwtsecurity.exception.InvalidRefreshToken;
import com.alfonso.jwtsecurity.exception.UserAlreadyExists;
import com.alfonso.jwtsecurity.exception.UserNotFoundException;
import com.alfonso.jwtsecurity.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
            throw new UserAlreadyExists("User already exists");
        }

        User user = User.builder().username(registerRequest.getUsername()).password(passwordEncoder.encode(registerRequest.getPassword())).fullname(registerRequest.getFullname()).role(registerRequest.getRole()).build();

        this.userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String username = jwtService.extractUsername(authentication);
            String fullname = jwtService.extractFullName(authentication);
            List<String> roles = jwtService.extractRoles(authentication);
            TokenPair tokenPair = jwtService.generateTokenPair(authentication);

            return new AuthResponse(username, fullname, roles, tokenPair);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }

    public AuthResponse refreshToken(@Valid RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        //check if it is valid refresh token
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidRefreshToken("Invalid refresh token");
        }

        String user = jwtService.extractUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user);

        if (userDetails == null) {
            throw new UserNotFoundException("User not found");
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String username = jwtService.extractUsername(authentication);
        String fullname = jwtService.extractFullName(authentication);
        List<String> roles = jwtService.extractRoles(authentication);
        TokenPair tokenPair = jwtService.generateTokenPair(authentication);

        return new AuthResponse(username, fullname, roles, tokenPair);
    }
}
