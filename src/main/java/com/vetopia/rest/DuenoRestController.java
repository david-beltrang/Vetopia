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

import com.vetopia.entities.Dueno;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.service.dueno.DuenoService;
import com.vetopia.service.mascota.MascotaService;

/*
 Controlador REST de cliente. Reemplaza al flujo con vistas del
 portal: en vez de devolver plantillas Thymeleaf, los métodos devuelven los
 objetos y Spring los serializa a JSON
*/
@RestController
@RequestMapping("/dueno")
@CrossOrigin(origins = "http://localhost:4200")
public class DuenoRestController {

    @Autowired
    private DuenoService duenoService;

    @Autowired
    private MascotaService mascotaService;

    //http://localhost:8090/dueno/all
    // Lista todos los clientes registrados
    @Operation(summary = "Lista todos los clientes")
    @GetMapping("/all")
    public List<Dueno> listar() {
        return duenoService.listarDuenos();
    }

    //http://localhost:8090/dueno/1
    // Un cliente concreto por su id
    @Operation(summary = "Obtiene un cliente por su id")
    @GetMapping("/{id}")
    public Dueno buscarPorId(@PathVariable Long id) {
        return duenoService.obtenerDuenoPorId(id);
    }

    //http://localhost:8090/dueno/1/mascotas
    // Mascotas de un cliente, usa la consulta del findByDuenoIdmediante el service
    // Valida el dueño para devolver 404 si el id no existe
    @Operation(summary = "Lista las mascotas de un cliente")
    @GetMapping("/{id}/mascotas")
    public List<Mascota> listarMascotas(@PathVariable Long id) {
        duenoService.obtenerDuenoPorId(id);
        return mascotaService.listarMascotasDeCliente(id);
    }

    //http://localhost:8090/dueno
    // Devuelve el cliente con el id ya asignado por la base de datos, porque
    // el frontend lo necesita para registrar sus mascotas justo después
    @Operation(summary = "Registra un nuevo cliente")
    @PostMapping
    public Dueno agregar(@RequestBody Dueno dueno) {
        dueno.setId(null);
        return duenoService.registrarClienteActivo(dueno);
    }

    //http://localhost:8090/dueno/1
    // El id viaja en la URL, así que JPA actualiza el registro existente
    @Operation(summary = "Actualiza un cliente existente")
    @PutMapping("/{id}")
    public Dueno actualizar(@PathVariable Long id, @RequestBody Dueno dueno) {
        dueno.setId(id);
        duenoService.guardar(dueno);
        return dueno;
    }

    //http://localhost:8090/dueno/1
    // Borra el cliente y, en cascada, sus mascotas y tratamientos
    @Operation(summary = "Elimina un cliente por su id")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        duenoService.eliminarCliente(id);
    }
}
