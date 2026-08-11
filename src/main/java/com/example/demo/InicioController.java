package com.example.demo;

import com.example.demo.model.Alumno;
import com.example.demo.repository.AlumnoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class InicioController {

    private final AlumnoRepository alumnoRepository;

    public InicioController(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;
    }

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @PostMapping("/alumnos")
    public String guardarAlumno(@ModelAttribute Alumno alumno) {
        alumnoRepository.save(alumno);
        return "redirect:/";
    }
}