package com.vetopia.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.vetopia.dto.AdministradorDTO;
import com.vetopia.entities.Administrador;
import com.vetopia.security.JWTGenerator;
import com.vetopia.service.admin.AdministradorService;


@RestController
@RequestMapping("/administrador")
@CrossOrigin(origins = "http://localhost:4200")
public class AdministradorRestController {

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired 
    JWTGenerator jwtGenerator;

    //http://localhost:8090/administrador/all
    // Se listan todos los administradores disponibles en la base de datos.
    @Operation(summary = "Lista todos los administradores")
    @GetMapping("/all")
    public ResponseEntity<List<AdministradorDTO>> listar() {
        List<AdministradorDTO> administradores = AdministradorDTO
                .from(administradorService.listarAdministradores());
        return ResponseEntity.ok(administradores);
    }

    //http://localhost:8090/administrador/1
    // Se obtiene un administrador por su id, si no se encuentra, se lanza una excepción
    @Operation(summary = "Obtiene un administrador por su id")
    @GetMapping("/{id}")
    public ResponseEntity<AdministradorDTO> buscarPorId(@PathVariable Long id) {
        Administrador administrador = administradorService.obtenerAdministradorPorId(id);
        return ResponseEntity.ok(AdministradorDTO.from(administrador));
    }

    //http://localhost:8090/administrador/login
    @PostMapping("/login")
    public ResponseEntity loginAdministrador(@RequestBody Administrador administrador) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(administrador.getCorreo(), administrador.getContrasena())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);

        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

    //http://localhost:8090/administrador
    // Se limpia el id entrante para que la base de datos asigne uno nuevo
    @Operation(summary = "Registra un nuevo administrador")
    @PostMapping
    public ResponseEntity<AdministradorDTO> agregar(@RequestBody Administrador administrador) {
        administrador.setId(null);
        administradorService.guardar(administrador);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AdministradorDTO.from(administrador));
    }

    //http://localhost:8090/administrador/1
    // Se actualiza un administrador existente, si no se encuentra, se lanza una excepción
    @Operation(summary = "Actualiza un administrador existente")
    @PutMapping("/{id}")
    public ResponseEntity<AdministradorDTO> actualizar(@PathVariable Long id,
            @RequestBody Administrador administrador) {
        administrador.setId(id);
        administradorService.guardar(administrador);
        Administrador administradorActualizado = administradorService.obtenerAdministradorPorId(id);
        return ResponseEntity.ok(AdministradorDTO.from(administradorActualizado));
    }

    //http://localhost:8090/administrador/1
    // Se elimina un administrador por su id, si no se encuentra, se lanza una excepción
    @Operation(summary = "Elimina un administrador por su id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        administradorService.eliminarAdministrador(id);
        return ResponseEntity.noContent().build();
    }
}
