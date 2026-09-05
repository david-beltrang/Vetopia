package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Dueno;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de dueños en la aplicación
public interface DuenoRepository {

    // Devuelve una colección de todos los dueños almacenados
    Collection<Dueno> searchAll();

    Dueno searchById(Integer id);

    void save(Dueno dueno);

    boolean eliminar(Integer id);

    // Busca un dueño por su correo electrónico y contraseña, devolviendo el objeto Dueno correspondiente si se encuentra una coincidencia.
    Dueno searchByCorreoYContrasena(String correo, String contrasena);
}
