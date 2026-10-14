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

import com.vetopia.entities.mascota.Mascota;
import com.vetopia.service.mascota.MascotaService;

/*
 Controlador REST de mascotas. El dueño llega por parámetro de URL
 y la mascota por el cuerpo JSON; la foto se maneja como una URL dentro del
 campo "imagen"
*/
@RestController
@RequestMapping("/mascota")
@CrossOrigin(origins = "http://localhost:4200")
public class MascotaRestController {

    @Autowired
    private MascotaService mascotaService;

    // Lista todas las mascotas
    @Operation(summary = "Lista todas las mascotas")
    @GetMapping("/all")
    public List<Mascota> listar() {
        return mascotaService.listarMascotas();
    }

    // Una mascota concreta por su id
    @Operation(summary = "Obtiene una mascota por su id")
    @GetMapping("/{id}")
    public Mascota buscarPorId(@PathVariable Long id) {
        return mascotaService.obtenerMascotaPorId(id);
    }

    // Mascotas de un dueño (consulta derivada findByDuenoId)
    @Operation(summary = "Lista las mascotas de un dueño")
    @GetMapping("/dueno/{duenoId}")
    public List<Mascota> listarPorDueno(@PathVariable Long duenoId) {
        return mascotaService.listarMascotasDeCliente(duenoId);
    }

    // Búsqueda por nombre (consulta derivada findByNombreContainingIgnoreCase)
    @Operation(summary = "Busca mascotas por nombre")
    @GetMapping("/buscar")
    public List<Mascota> buscarPorNombre(@RequestParam String nombre) {
        return mascotaService.buscarPorNombre(nombre);
    }

    // El service valida el dueño, fija el estado en Activo y conserva la imagen
    // que venga en el JSON. Se retorna null en el archivo porque no hay subida de imagen
    @Operation(summary = "Registra una nueva mascota")
    @PostMapping
    public Mascota agregar(@RequestBody Mascota mascota, @RequestParam Long duenoId) {
        mascota.setId(null);
        return mascotaService.registrarMascota(mascota, duenoId, null);
    }

    // Igual que en el POST, sin foto: se conservan dueño, estado e imagen
    // cuando no vienen en el JSON
    @Operation(summary = "Actualiza una mascota existente")
    @PutMapping("/{id}")
    public Mascota actualizar(@PathVariable Long id, @RequestBody Mascota mascota) {
        mascota.setId(id);
        return mascotaService.actualizarMascota(mascota, null);
    }

    // Cambia el estado de la mascota de activo a inactivo o al reves.
    // El estado llega por la URL como texto ("Activo")
    @Operation(summary = "Cambia el estado (activo/inactivo) de una mascota")
    @PutMapping("/{id}/estado")
    public void cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        mascotaService.cambiarEstado(id, estado);
    }

    // Borra la mascota y, en cascada, sus tratamientos.
    @Operation(summary = "Elimina una mascota por su id")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        mascotaService.eliminarMascota(id);
    }
}
