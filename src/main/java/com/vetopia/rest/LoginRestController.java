package com.vetopia.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import com.vetopia.dto.LoginRequestDTO;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.exception.CredencialesInvalidasException;
import com.vetopia.service.login.LoginService;
import com.vetopia.service.login.ResultadoLogin;
import com.vetopia.service.veterinario.VeterinarioService;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class LoginRestController {

    @Autowired
    private LoginService loginService;
    private VeterinarioService veterinarioService;

    //http://localhost:8090/auth/login
    // Autentica un usuario (veterinario, administrador o cliente) y 
    // devuelve un objeto ResultadoLogin con la información del usuario y su rol
    @Operation(summary = "Autentica un usuario (veterinario, administrador o cliente)")
    @PostMapping("/login")
    public ResultadoLogin login(@RequestBody LoginRequestDTO credenciales) {
        ResultadoLogin resultado = loginService.autenticar(
                credenciales.getCorreo(), credenciales.getContrasena());

        // Si no coincide con ningún veterinario, administrador ni cliente, se
        // lanza la excepción y RestExceptionHandler responde 401 en lugar de
        // un 200 con cuerpo vacío.
        if (resultado == null) {
            throw new CredencialesInvalidasException("Credenciales incorrectas.");
        } else if (DominioEstado.INACTIVO.equals(resultado.getEstado())){
            throw new CredencialesInvalidasException("Usuario Inactivo");
        }
        return resultado;
    }
}
