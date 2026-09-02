package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Administrador;

//Esta es la interfaz que define los métodos que debe implementar cualquier clase que funcione como repositorio de administradores.
public interface AdministradorRepository {

    // Devuelve todos los administradores registrados.
    Collection<Administrador> searchAll();

    // Busca un administrador por su identificador dentro del HashMap.
    Administrador searchById(Integer id);

    // Guarda un administrador en el HashMap asignándole automáticamente el siguiente id disponible.
    void save(Administrador administrador);

    // Busca un administrador por su correo y contraseña
    Administrador searchByCorreoYContrasena(String correo, String contrasena);
}
