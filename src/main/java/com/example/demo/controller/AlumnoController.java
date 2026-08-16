/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

//imports de las dependencias para MAVEN y realizar la api rest
import com.example.demo.repository.AlumnoRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//importa para crear el endpoinst para listar
import com.example.demo.model.Alumno;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

//import para el POST 
import org.springframework.web.bind.annotation.PostMapping;

//Import para el PUT
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

//Import para el DELETE
import org.springframework.web.bind.annotation.DeleteMapping;

//Controller de Alumnos
@RestController
@RequestMapping("/api/alumnos")
@Tag(name = "Alumnos", description = "API para administrar alumnos")
public class AlumnoController {

    private final AlumnoRepository alumnoRepository;//obtenemos la referencia del repoditorio del alumno

    public AlumnoController(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;
    }

    //GET: Endpoint para listar los usuarios
    @Operation(summary = "Listar todos los alumnos")
    @GetMapping
    public List<Alumno> listar() {
        return alumnoRepository.findAll();
    }

    //Endpouint para crear al alumno
    @Operation(summary = "Crear un alumno")
    @PostMapping
    public ResponseEntity<Alumno> crear(@RequestBody Alumno alumno) {

        if (alumno.getDni() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (alumnoRepository.existsById(alumno.getDni())) {
            return ResponseEntity.status(409).build();
        }

        Alumno alumnoCreado = alumnoRepository.save(alumno);

        return ResponseEntity.status(201).body(alumnoCreado);
    }

    //PUT: Endpoint para modificar al alumno
    @Operation(summary = "Modificar un alumno")
    //@PreAuthorize("hasRole('ADMIN')")
    @Hidden
    @PutMapping("/{dni}")
    public ResponseEntity<Alumno> modificar(@PathVariable Integer dni, @RequestBody Alumno alumno) {
        if (!alumnoRepository.existsById(dni)) {
            return ResponseEntity.notFound().build();
        }

        alumno.setDni(dni);
        Alumno actualizado = alumnoRepository.save(alumno);

        return ResponseEntity.ok(actualizado);
    }

    // DELETE: Endpoint para eliminar un alumno por DNI
    @Operation(summary = "Eliminar un alumno")
    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer dni) {

        if (!alumnoRepository.existsById(dni)) {
            return ResponseEntity.notFound().build();
        }

        alumnoRepository.deleteById(dni);

        return ResponseEntity.noContent().build();
    }

}
