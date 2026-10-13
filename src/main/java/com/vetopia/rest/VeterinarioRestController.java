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

import com.vetopia.entities.Veterinario;
import com.vetopia.service.veterinario.VeterinarioService;

// Controlador REST de veterinarios. Lo usa el frontend sobre todo para mostrar
// el nombre en el header del portal; se deja el CRUD completo por simetría.
@RestController
@RequestMapping("/veterinario")
@CrossOrigin(origins = "http://localhost:4200")
public class VeterinarioRestController {

    @Autowired
    private VeterinarioService veterinarioService;

    @GetMapping("/all")
    public List<Veterinario> listar() {
        return veterinarioService.listarVeterinarios();
    }

    @GetMapping("/{id}")
    public Veterinario buscarPorId(@PathVariable Long id) {
        return veterinarioService.obtenerVeterinarioPorId(id);
    }

    // El service asigna el id; se devuelve el objeto para reutilizarlo en Angular.
    // Se limpia el id entrante para que siempre sea un alta y no una actualización.
    @PostMapping("/add")
    public Veterinario agregar(@RequestBody Veterinario veterinario) {
        veterinario.setId(null);
        veterinarioService.guardar(veterinario);
        return veterinario;
    }

    @PutMapping("/update")
    public Veterinario actualizar(@RequestBody Veterinario veterinario) {
        veterinarioService.guardar(veterinario);
        return veterinario;
    }

    @DeleteMapping("/delete/{id}")
    public void eliminar(@PathVariable Long id) {
        veterinarioService.eliminarVeterinario(id);
    }
}
