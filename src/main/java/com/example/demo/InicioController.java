package com.example.demo;

import com.example.demo.model.Alumno;
import com.example.demo.repository.AlumnoRepository;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    //Metodos para buscar y modificar un Alumno

    @GetMapping("/alumnos/buscar")
    public String buscarAlumno(@RequestParam Integer dni, Model model) {
        Alumno alumno = alumnoRepository.findById(dni).orElse(null);

        if (alumno == null) {
            model.addAttribute("mensaje", "Alumno no encontrado");
            return "index";
        }
        model.addAttribute("alumno", alumno);
        return "editar";
    }

    @PostMapping("/alumnos/editar")
    public String modificarAlumno(@ModelAttribute Alumno alumno) {
        alumnoRepository.save(alumno);
        return "redirect:/lista";
    }

    //Listado para consumir desde templates/ListaAlumnos.html utilizando Thymeleaf utilizando este controller (InicioController, porque devuelve una página HTML de Thymeleaf)
    @GetMapping("/lista")
    public String mostrarLista(Model model) {

        List<Alumno> alumnos = alumnoRepository.findAll();
        model.addAttribute("alumnos", alumnos);

        return "ListaAlumnos";
    }

    @PostMapping("/alumnos/eliminar")
    public String eliminarAlumno(@RequestParam Integer dni) {

        if (alumnoRepository.existsById(dni)) {
            alumnoRepository.deleteById(dni);
        }

        return "redirect:/lista";
    }
    
    
    ////controller de consumo de la api
    @GetMapping("/lista-api")
    public String mostrarListaApi() {
        return "ListaAlumnosApi";
    }
}
