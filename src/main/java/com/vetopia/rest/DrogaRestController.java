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

import com.vetopia.entities.Droga;
import com.vetopia.service.droga.DrogaService;

// Controlador REST del inventario de medicamentos. El frontend lo necesita
// para llenar el selector al asignar un tratamiento.
@RestController
@RequestMapping("/droga")
@CrossOrigin(origins = "http://localhost:4200")
public class DrogaRestController {

    @Autowired
    private DrogaService drogaService;

    @GetMapping("/all")
    public List<Droga> listar() {
        return drogaService.listarDrogas();
    }

    @GetMapping("/{id}")
    public Droga buscarPorId(@PathVariable Long id) {
        return drogaService.obtenerDrogaPorId(id);
    }

    // Se limpia el id entrante para que la base de datos asigne uno nuevo.
    @PostMapping("/add")
    public Droga agregar(@RequestBody Droga droga) {
        droga.setId(null);
        drogaService.guardar(droga);
        return droga;
    }

    @PutMapping("/update")
    public Droga actualizar(@RequestBody Droga droga) {
        drogaService.guardar(droga);
        return droga;
    }

    @DeleteMapping("/delete/{id}")
    public void eliminar(@PathVariable Long id) {
        drogaService.eliminarDroga(id);
    }
}
