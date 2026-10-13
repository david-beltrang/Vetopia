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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import com.vetopia.dto.HistorialTratamientoDTO;
import com.vetopia.dto.ResultadoTratamientoDTO;
import com.vetopia.entities.Tratamiento;
import com.vetopia.service.tratamiento.TratamientoService;

/*
 Controlador REST de tratamientos. Además del CRUD se exponen las consultas
 derivadas del repositorio por mascota y por veterinario y la operación de
 negocio "asignar", que crea el tratamiento y descuenta el inventario.
*/
@RestController
@RequestMapping("/tratamiento")
@CrossOrigin(origins = "http://localhost:4200")
public class TratamientoRestController {

    @Autowired
    private TratamientoService tratamientoService;

    @Operation(summary = "Lista todos los tratamientos")
    @GetMapping("/all")
    public List<Tratamiento> listar() {
        return tratamientoService.listarTratamientos();
    }

    @Operation(summary = "Obtiene un tratamiento por su id")
    @GetMapping("/{id}")
    public Tratamiento buscarPorId(@PathVariable Long id) {
        return tratamientoService.obtenerTratamientoPorId(id);
    }

    // Tratamientos de una mascota (consulta derivada findByMascotaId)
    @Operation(summary = "Lista los tratamientos de una mascota")
    @GetMapping("/mascota/{mascotaId}")
    public List<Tratamiento> listarPorMascota(@PathVariable Long mascotaId) {
        return tratamientoService.listarTratamientosPorMascota(mascotaId);
    }

    // Historial de una mascota con el nombre de la droga, es lo que pinta la
    // ficha clínica. Se devuelve el DTO y no la entidad para que el frontend
    // reciba el nombre del medicamento ya resuelto.
    @Operation(summary = "Historial de tratamientos de una mascota (con nombre de droga)")
    @GetMapping("/mascota/{mascotaId}/historial")
    public List<HistorialTratamientoDTO> historialDeMascota(@PathVariable Long mascotaId) {
        return tratamientoService.listarHistorialDeMascota(mascotaId);
    }

    // Tratamientos de un veterinario (consulta derivada findByVeterinarioId)
    @Operation(summary = "Lista los tratamientos de un veterinario")
    @GetMapping("/veterinario/{veterinarioId}")
    public List<Tratamiento> listarPorVeterinario(@PathVariable Long veterinarioId) {
        return tratamientoService.listarTratamientosPorVeterinario(veterinarioId);
    }

    @Operation(summary = "Historial de tratamientos de un veterinario (con nombre de droga)")
    @GetMapping("/veterinario/{veterinarioId}/historial")
    public List<HistorialTratamientoDTO> historialDeVeterinario(@PathVariable Long veterinarioId) {
        return tratamientoService.listarTratamientosPorVeterinarioConDroga(veterinarioId);
    }

    // Alta simple: guarda el tratamiento tal cual llega, sin descontar stock.
    // Se limpia el id para que la base de datos asigne uno nuevo.
    @Operation(summary = "Registra un nuevo tratamiento")
    @PostMapping
    public Tratamiento agregar(@RequestBody Tratamiento tratamiento) {
        tratamiento.setId(null);
        tratamientoService.guardar(tratamiento);
        return tratamiento;
    }

    @Operation(summary = "Actualiza un tratamiento existente")
    @PutMapping("/{id}")
    public Tratamiento actualizar(@PathVariable Long id, @RequestBody Tratamiento tratamiento) {
        tratamiento.setId(id);
        tratamientoService.guardar(tratamiento);
        return tratamiento;
    }

    // Asignar un tratamiento a una mascota. En el JSON llegan la mascota y la
    // droga con su id; el veterinario viene por la URL. Se limpia el id para
    // que sea siempre un alta y no una actualización accidental.
    @Operation(summary = "Asigna un tratamiento a una mascota y descuenta inventario")
    @PostMapping("/asignar")
    public ResultadoTratamientoDTO asignar(@RequestBody Tratamiento tratamiento,
            @RequestParam Long veterinarioId) {
        tratamiento.setId(null);
        return tratamientoService.asignarTratamiento(tratamiento, veterinarioId);
    }

    @Operation(summary = "Elimina un tratamiento por su id")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        tratamientoService.eliminarTratamiento(id);
    }
}
