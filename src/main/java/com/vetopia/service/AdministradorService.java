package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Administrador;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de administradores en la aplicación.
public interface AdministradorService {

    // Devuelve todos los administradores registrados.
    List<Administrador> listarAdministradores();

    // Busca un administrador por su identificador.
    Administrador obtenerAdministradorPorId(Integer id);

    // Guarda un administrador en la base de datos.
    void guardar(Administrador administrador);

    // Busca un administrador por su correo y contraseña.
    Administrador obtenerPorCorreoYContrasena(String correo, String contrasena);
}
