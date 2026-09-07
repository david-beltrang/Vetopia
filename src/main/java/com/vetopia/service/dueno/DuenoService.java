package com.vetopia.service.dueno;

import java.util.List;

import com.vetopia.entities.Dueno;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de dueños en la aplicación.
public interface DuenoService {

    List<Dueno> listarDuenos();

    Dueno obtenerDuenoPorId(Long id);

    void guardar(Dueno dueno);

    // Registra un dueño nuevo con estado "Activo" y devuelve el objeto
    // resultante para poder obtener su id tras el guardado.
    Dueno registrarClienteActivo(Dueno dueno);

    void eliminarCliente(Long id);

    // Busca un dueño por correo y contraseña (para login).
    Dueno obtenerPorCorreoYContrasena(String correo, String contrasena);
}