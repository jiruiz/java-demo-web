package com.example.demo;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner crearAdmin(
            UserRepository repository,
            UserService userService) {

        return args -> {
            if (repository.findByUsuario("admin").isEmpty()) {
                User user = new User();
                user.setUsuario("admin");
                user.setClave("1234");
                user.setDatosPersonales("Administrador");
                user.setRole(Role.ADMIN);

                userService.guardar(user);
            }
        };
    }
}