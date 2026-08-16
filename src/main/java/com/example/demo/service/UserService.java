package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User guardar(User user) {
        user.setClave(passwordEncoder.encode(user.getClave()));
        return userRepository.save(user);
    }

    public boolean claveCorrecta(User user, String claveIngresada) {
        return passwordEncoder.matches(claveIngresada, user.getClave());
    }
}
