package com.closed_sarc.app_reservation_api.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    // Credenciais do usuário master
    private static final String MASTER_EMAIL = "master@reservation.com";
    private static final String MASTER_PASSWORD = "master123";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (!MASTER_EMAIL.equals(email)) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + email);
        }

        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_MASTER")
        );

        return User.builder()
                .username(MASTER_EMAIL)
                .password(MASTER_PASSWORD) // Senha sem criptografia para Basic Auth
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
