package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Dueno;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de dueños en la aplicación.
public interface DuenoRepository {


    Collection<Dueno> searchAll();

    Dueno searchById(Integer id);

    void save(Dueno dueno);

    Dueno searchByCorreoYContrasena(String correo, String contrasena);
}
