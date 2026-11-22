package com.vetopia.service.dueno;

import java.util.List;

import com.vetopia.entities.Dueno;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de dueños en la aplicación
public interface DuenoService {

    List<Dueno> listarDuenos();

    // Obtiene un dueño validando previamente el identificador
    Dueno obtenerDuenoPorId(Long id);

    // Busca un dueño por su cédula
    Dueno obtenerPorCedula(String cedula);

    void guardar(Dueno dueno);

    // Registra un dueño nuevo y devuelve el objeto
    // resultante para poder obtener su id tras el guardado
    Dueno registrarClienteActivo(Dueno dueno);

    void eliminarCliente(Long id);

    // Busca un dueño por correo y contraseña (para login)
    Dueno obtenerPorCorreoYContrasena(String correo, String contrasena);

    // Busca un dueño por correo (usado en /details con el usuario autenticado)
    Dueno obtenerPorCorreo(String correo);
}