package com.vetopia.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetopia.dto.LoginRequestDTO;
import com.vetopia.service.login.LoginService;
import com.vetopia.service.login.ResultadoLogin;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class LoginRestController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<ResultadoLogin> login(@RequestBody LoginRequestDTO credenciales) {
        ResultadoLogin resultado = loginService.autenticar(
                credenciales.getCorreo(), credenciales.getContrasena());

        // Si no coincide con ningún veterinario, administrador ni cliente, se
        // responde 401 en lugar de un 200 con cuerpo vacío.
        if (resultado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(resultado);
    }
}
