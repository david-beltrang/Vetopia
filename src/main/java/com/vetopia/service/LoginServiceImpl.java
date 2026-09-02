package com.vetopia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Administrador;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Veterinario;
import com.vetopia.service.ResultadoLogin;
/*
 Busca las credenciales en cada tabla (delegando en los services de cada
 entidad hasta encontrar un match.
 */
@Service
public class LoginServiceImpl implements LoginService {


    private final String ROL_VETERINARIO = "VETERINARIO";
    private final String ROL_ADMINISTRADOR = "ADMINISTRADOR";
    private final String ROL_CLIENTE = "CLIENTE";

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private DuenoService duenoService;

    @Override
    public ResultadoLogin autenticar(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            return null;
        }

        Veterinario veterinario = veterinarioService.obtenerPorCorreoYContrasena(correo, contrasena);
        if (veterinario != null) {
            return new ResultadoLogin(veterinario.getId(), ROL_VETERINARIO, veterinario.getCorreo(), veterinario.getContrasena());
        }

        Administrador administrador = administradorService.obtenerPorCorreoYContrasena(correo, contrasena);
        if (administrador != null) {
            return new ResultadoLogin(administrador.getId(), ROL_ADMINISTRADOR, administrador.getCorreo(), administrador.getContrasena());
        }

        Dueno dueno = duenoService.obtenerPorCorreoYContrasena(correo, contrasena);
        if (dueno != null) {
            return new ResultadoLogin(dueno.getId(), ROL_CLIENTE, dueno.getCorreo(), dueno.getContrasena());
        }

        return null;
    }
}
