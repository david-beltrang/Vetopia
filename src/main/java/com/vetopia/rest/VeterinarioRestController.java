package com.vetopia.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    //http://localhost:8090/veterinario/all
    // Se deja el CRUD completo por simetría, aunque el frontend solo usa listar y buscarPorId
    @Operation(summary = "Lista todos los veterinarios")
    @GetMapping("/all")
    public ResponseEntity<List<Veterinario>> listar() {

        List<Veterinario> veterinarios = veterinarioService.listarVeterinarios();

        ResponseEntity<List<Veterinario>> response = new ResponseEntity<>(veterinarios, HttpStatus.OK);

        return response;
    }

    //http://localhost:8090/veterinario/1
    // Se deja el CRUD completo por simetría, aunque el frontend solo usa listar y buscarPorId
    @Operation(summary = "Obtiene un veterinario por su id")
    @GetMapping("/{id}")
    public ResponseEntity<Veterinario> buscarPorId(@PathVariable Long id) {

        Veterinario veterinario = veterinarioService.obtenerVeterinarioPorId(id);

        return new ResponseEntity<>(veterinario, HttpStatus.OK);
    }

    //http://localhost:8090/veterinario
    // El service asigna el id, se devuelve el objeto para reutilizarlo en Angular.
    // Se limpia el id entrante para que siempre sea un alta y no una actualización.
    @Operation(summary = "Registra un nuevo veterinario")
    @PostMapping
    public ResponseEntity<Veterinario> agregar(@RequestBody Veterinario veterinario) {
        veterinario.setId(null);
        veterinarioService.guardar(veterinario);
        return new ResponseEntity<>(veterinario, HttpStatus.CREATED);
    }

    //http://localhost:8090/veterinario/1
    // Se deja el CRUD completo por simetría, aunque el frontend solo usa listar y buscarPorId
    @Operation(summary = "Actualiza un veterinario existente")
    @PutMapping("/{id}")
    public ResponseEntity<Veterinario> actualizar(@PathVariable Long id, @RequestBody Veterinario veterinario) {
        veterinarioService.obtenerVeterinarioPorId(id);
        veterinario.setId(id);
        veterinarioService.guardar(veterinario);
        return new ResponseEntity<>(veterinario, HttpStatus.OK);
    }

    //http://localhost:8090/veterinario/1/estado
    // Modificar el estado de un veterinario "Activo"-"Inactivo"
    @Operation(summary = "Cambia el estado de un veterinario")
    @PutMapping("/{id}/estado")
    public ResponseEntity<Veterinario> cambiarEstado(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        // Conserva los datos actuales y modifica únicamente el estado recibido.
        if (body == null || body.get("estado") == null || body.get("estado").isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Veterinario veterinario = veterinarioService.obtenerVeterinarioPorId(id);
        String estadoStr = body.get("estado");
        veterinario.setEstado(com.vetopia.entities.mascota.dominio_attr.DominioEstado.fromValor(estadoStr));
        veterinarioService.guardar(veterinario);
        return new ResponseEntity<>(veterinario, HttpStatus.OK);
    }

    //http://localhost:8090/veterinario/1
    // Se deja el CRUD completo por simetría, aunque el frontend solo usa listar y buscarPorId
    @Operation(summary = "Elimina un veterinario por su id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        veterinarioService.eliminarVeterinario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //http://localhost:8090/veterinario/activos/count
    // Contar cuantos veterinarios hay activos
    @Operation(summary = "Cantidad de veterinarios activos")
    @GetMapping("/activos/count")
    public ResponseEntity<Long> contarActivos() {
        return new ResponseEntity<>(veterinarioService.contarVeterinariosActivos(), HttpStatus.OK);
    }

    //http://localhost:8090/veterinario/inactivos/count
    // Contar cuantos veterinarios hay inactivos
    @Operation(summary = "Cantidad de veterinarios inactivos")
    @GetMapping("/inactivos/count")
    public ResponseEntity<Long> contarInactivos() {
        return new ResponseEntity<>(veterinarioService.contarVeterinariosInactivos(), HttpStatus.OK);
    }

    //http://localhost:8090/veterinario/1/atenciones/count
    // Contar cantidad de tratamienots aplicados del veterinario
    @Operation(summary = "Cantidad de tratamientos aplicados por un veterinario")
    @GetMapping("/{id}/atenciones/count")
    public ResponseEntity<Long> contarAtenciones(@PathVariable Long id) {
        veterinarioService.obtenerVeterinarioPorId(id);
        return new ResponseEntity<>(veterinarioService.contarAtenciones(id), HttpStatus.OK);
    }
}
