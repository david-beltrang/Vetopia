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

import com.vetopia.entities.Administrador;
import com.vetopia.service.admin.AdministradorService;


@RestController
@RequestMapping("/administrador")
@CrossOrigin(origins = "http://localhost:4200")
public class AdministradorRestController {

    @Autowired
    private AdministradorService administradorService;

    @GetMapping("/all")
    public List<Administrador> listar() {
        return administradorService.listarAdministradores();
    }

    @GetMapping("/{id}")
    public Administrador buscarPorId(@PathVariable Long id) {
        return administradorService.obtenerAdministradorPorId(id);
    }

    // Se limpia el id entrante para que la base de datos asigne uno nuevo.
    @PostMapping("/add")
    public Administrador agregar(@RequestBody Administrador administrador) {
        administrador.setId(null);
        administradorService.guardar(administrador);
        return administrador;
    }

    @PutMapping("/update")
    public Administrador actualizar(@RequestBody Administrador administrador) {
        administradorService.guardar(administrador);
        return administrador;
    }

    @DeleteMapping("/delete/{id}")
    public void eliminar(@PathVariable Long id) {
        administradorService.eliminarAdministrador(id);
    }
}
