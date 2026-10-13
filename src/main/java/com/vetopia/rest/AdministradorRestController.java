package com.vetopia.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import com.vetopia.entities.Administrador;
import com.vetopia.service.admin.AdministradorService;


@RestController
@RequestMapping("/administrador")
@CrossOrigin(origins = "http://localhost:4200")
public class AdministradorRestController {

    @Autowired
    private AdministradorService administradorService;

    @Operation(summary = "Lista todos los administradores")
    @GetMapping("/all")
    public List<Administrador> listar() {
        return administradorService.listarAdministradores();
    }

    @Operation(summary = "Obtiene un administrador por su id")
    @GetMapping("/{id}")
    public Administrador buscarPorId(@PathVariable Long id) {
        return administradorService.obtenerAdministradorPorId(id);
    }

    // Se limpia el id entrante para que la base de datos asigne uno nuevo.
    @Operation(summary = "Registra un nuevo administrador")
    @PostMapping
    public Administrador agregar(@RequestBody Administrador administrador) {
        administrador.setId(null);
        administradorService.guardar(administrador);
        return administrador;
    }

    @Operation(summary = "Actualiza un administrador existente")
    @PutMapping("/{id}")
    public Administrador actualizar(@PathVariable Long id, @RequestBody Administrador administrador) {
        administrador.setId(id);
        administradorService.guardar(administrador);
        return administrador;
    }

    @Operation(summary = "Elimina un administrador por su id")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        administradorService.eliminarAdministrador(id);
    }
}
