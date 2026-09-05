package com.vetopia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Administrador;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Veterinario;
/*
 Busca las credenciales en cada tabla (delegando en los services de cada
 entidad hasta encontrar un match.
 */
@Service
public class LoginServiceImpl implements LoginService {

    // Constantes para los roles de usuario existtentes
    private final String ROL_VETERINARIO = "VETERINARIO";
    private final String ROL_ADMINISTRADOR = "ADMINISTRADOR";
    private final String ROL_CLIENTE = "CLIENTE";

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private DuenoService duenoService;

    // Autentica un usuario buscando sus credenciales en las tablas de veterinarios, administradores y dueños
    @Override
    public ResultadoLogin autenticar(String correo, String contrasena) {
        // Validamos que el correo y la contraseña no sean nulos o vacíos antes de buscar en los servicios
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            return null;
        }

        // Buscamos primero en la tabla de veterinarios
        Veterinario veterinario = veterinarioService.obtenerPorCorreoYContrasena(correo, contrasena);
        if (veterinario != null) {
            return new ResultadoLogin(veterinario.getId(), ROL_VETERINARIO, veterinario.getCorreo(), veterinario.getContrasena());
        }

        // Si no se encuentra en veterinarios, buscamos en la tabla de administradores
        Administrador administrador = administradorService.obtenerPorCorreoYContrasena(correo, contrasena);
        // Si se encuentra un administrador, devolvemos el resultado de login con el rol correspondiente
        if (administrador != null) {
            return new ResultadoLogin(administrador.getId(), ROL_ADMINISTRADOR, administrador.getCorreo(), administrador.getContrasena());
        }

        // Si no se encuentra en administradores, buscamos en la tabla de dueños
        Dueno dueno = duenoService.obtenerPorCorreoYContrasena(correo, contrasena);
        // Si se encuentra un dueño, devolvemos el resultado de login con el rol correspondiente
        if (dueno != null) {
            return new ResultadoLogin(dueno.getId(), ROL_CLIENTE, dueno.getCorreo(), dueno.getContrasena());
        }

        // Si no se encuentra en ninguna tabla, devolvemos null indicando que las credenciales no son válidas
        return null;
    }
}
