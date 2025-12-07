package com.alfonso.jwtsecurity.service;

import com.alfonso.jwtsecurity.details.CustomUserDetails;
import com.alfonso.jwtsecurity.entity.User;
import com.alfonso.jwtsecurity.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
//@RequiredArgsConstructor -> solo invoca a los atributos final y @NonNull
class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        System.out.println("==== loadUserByUsername ==== ");
        return new CustomUserDetails(user);
    }

    // Devuelve algo como: [ { authority: "ADMIN" } ]
    private Collection<? extends GrantedAuthority> getAuthority(User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
        return List.of(authority);
    }
}
