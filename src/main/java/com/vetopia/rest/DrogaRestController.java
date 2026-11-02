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

import com.vetopia.entities.Droga;
import com.vetopia.service.droga.DrogaService;

// Controlador REST del inventario de medicamentos. El frontend lo necesita
// para llenar el selector al asignar un tratamiento
@RestController
@RequestMapping("/droga")
@CrossOrigin(origins = "http://localhost:4200")
public class DrogaRestController {

    @Autowired
    private DrogaService drogaService;

    //http://localhost:8090/droga/all
    // Se listan todas las drogas disponibles en la base de datos
    @Operation(summary = "Lista todas las drogas")
    @GetMapping("/all")
    public List<Droga> listar() {
        return drogaService.listarDrogas();
    }

    //http://localhost:8090/droga/1
    // Se obtiene una droga por su id, si no se encuentra, se lanza una excepción
    @Operation(summary = "Obtiene una droga por su id")
    @GetMapping("/{id}")
    public Droga buscarPorId(@PathVariable Long id) {
        return drogaService.obtenerDrogaPorId(id);
    }

    //http://localhost:8090/droga
    // Se limpia el id entrante para que la base de datos asigne uno nuevo.
    @Operation(summary = "Registra una nueva droga")
    @PostMapping
    public Droga agregar(@RequestBody Droga droga) {
        droga.setId(null);
        drogaService.guardar(droga);
        return droga;
    }

    //http://localhost:8090/droga/1
    // Se actualiza una droga existente, si no se encuentra, se lanza una excepción
    @Operation(summary = "Actualiza una droga existente")
    @PutMapping("/{id}")
    public Droga actualizar(@PathVariable Long id, @RequestBody Droga droga) {
        droga.setId(id);
        drogaService.guardar(droga);
        return droga;
    }

    //http://localhost:8090/droga/1
    // Se elimina una droga por su id, si no se encuentra, se lanza una excepción
    @Operation(summary = "Elimina una droga por su id")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        drogaService.eliminarDroga(id);
    }
}
