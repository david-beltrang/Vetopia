package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Dueno;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de dueños en la aplicación.
public interface DuenoService {

    // Devuelve todos los dueños registrados.
    List<Dueno> listarDuenos();

    // Obtiene un dueño validando previamente el identificador.
    Dueno obtenerDuenoPorId(Integer id);

    // Guarda (registra) un dueño nuevo. El id lo asigna el repositorio.
    void guardar(Dueno dueno);

    // Registra un dueño nuevo con estado "Activo" y devuelve el objeto
    // resultante para poder obtener su id tras el guardado.
    Dueno registrarClienteActivo(Dueno dueno);

    // Elimina un dueño por su identificador.
    void eliminarCliente(Integer id);

    // Busca un dueño por correo y contraseña (para login).
    Dueno obtenerPorCorreoYContrasena(String correo, String contrasena);
}