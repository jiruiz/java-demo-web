package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String usuario) {
        User user = repository.findByUsuario(usuario)
                .orElseThrow(() ->
                    new UsernameNotFoundException("Usuario no encontrado")
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsuario())
                .password(user.getClave())
                .roles(user.getRole().name())
                .build();
    }
}