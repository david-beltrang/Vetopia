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

import com.vetopia.dto.DuenoDTO;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.UserEntity;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.repository.UserRepository;
import com.vetopia.security.CustomUserDetailService;
import com.vetopia.security.JWTGenerator;
import com.vetopia.service.dueno.DuenoService;
import com.vetopia.service.mascota.MascotaService;

/*
 Controlador REST de cliente. Reemplaza al flujo con vistas del
 portal: en vez de devolver plantillas Thymeleaf, los métodos devuelven DTOs
 (o la entidad Mascota, que no expone relaciones sensibles) y Spring los
 serializa a JSON.
*/
@RestController
@RequestMapping("/dueno")
@CrossOrigin(origins = "http://localhost:4200")
public class DuenoRestController {

    @Autowired
    private DuenoService duenoService;

    @Autowired
    private MascotaService mascotaService;

    @Autowired 
    UserRepository userRepository;

    @Autowired 
    CustomUserDetailService customUserDetailService;

    @Autowired 
    AuthenticationManager authenticationManager;

    @Autowired 
    JWTGenerator jwtGenerator;

    //http://localhost:8090/dueno/all
    // Lista todos los clientes registrados
    @Operation(summary = "Lista todos los clientes")
    @GetMapping("/all")
    public ResponseEntity<List<DuenoDTO>> listar() {
        List<DuenoDTO> duenos = DuenoDTO.from(duenoService.listarDuenos());
        return ResponseEntity.ok(duenos);
    }

    //http://localhost:8090/dueno/1
    // Un cliente concreto por su id
    @Operation(summary = "Obtiene un cliente por su id")
    @GetMapping("/{id}")
    public ResponseEntity<DuenoDTO> buscarPorId(@PathVariable Long id) {
        Dueno dueno = duenoService.obtenerDuenoPorId(id);
        return ResponseEntity.ok(DuenoDTO.from(dueno));
    }

    @PostMapping("/login")
    public ResponseEntity loginDueno(@RequestBody Dueno dueno) {
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dueno.getCorreo(), dueno.getContrasena())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);

        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

    //http://localhost:8090/dueno/details
    // Devuelve los datos del dueño autenticado a partir del correo que lleva el JWT
    @GetMapping("/details")
    public ResponseEntity<DuenoDTO> buscarDueno() {

        Dueno dueno = duenoService.obtenerPorCorreo(
            SecurityContextHolder.getContext().getAuthentication().getName()
        );

        if (dueno == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(DuenoDTO.from(dueno), HttpStatus.OK);
    }


    //http://localhost:8090/dueno/1/mascotas
    // Mascotas de un cliente, usa la consulta del findByDuenoId mediante el service
    // Valida el dueño para devolver 404 si el id no existe
    @Operation(summary = "Lista las mascotas de un cliente")
    @GetMapping("/{id}/mascotas")
    public ResponseEntity<List<Mascota>> listarMascotas(@PathVariable Long id) {
        duenoService.obtenerDuenoPorId(id);
        return ResponseEntity.ok(mascotaService.listarMascotasDeCliente(id));
    }

    //http://localhost:8090/dueno
    // Devuelve el cliente con el id ya asignado por la base de datos, porque
    // el frontend lo necesita para registrar sus mascotas justo después
    @Operation(summary = "Registra un nuevo cliente")
    @PostMapping
    public ResponseEntity<Dueno> agregar(@RequestBody Dueno dueno) {

        if (userRepository.existsByUsername(dueno.getCorreo())) {
            return new ResponseEntity<Dueno>(dueno, HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = customUserDetailService.DuenoToUser(dueno);
        dueno.setUser(userEntity);
        Dueno newDueno = duenoService.registrarClienteActivo(dueno);
    
        if(newDueno == null) {
            return new ResponseEntity<Dueno>(newDueno, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Dueno>(newDueno, HttpStatus.CREATED);
    }

    //http://localhost:8090/dueno/1
    // El id viaja en la URL, así que JPA actualiza el registro existente
    @Operation(summary = "Actualiza un cliente existente")
    @PutMapping("/{id}")
    public ResponseEntity<DuenoDTO> actualizar(@PathVariable Long id, @RequestBody Dueno dueno) {
        dueno.setId(id);
        duenoService.guardar(dueno);
        Dueno duenoActualizado = duenoService.obtenerDuenoPorId(id);
        return ResponseEntity.ok(DuenoDTO.from(duenoActualizado));
    }

    //http://localhost:8090/dueno/1
    // Borra el cliente y, en cascada, sus mascotas y tratamientos
    @Operation(summary = "Elimina un cliente por su id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        duenoService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
