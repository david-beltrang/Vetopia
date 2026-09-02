package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Veterinario;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de veterinarios en la aplicación.
public interface VeterinarioRepository {

    // Devuelve todos los veterinarios registrados.
    Collection<Veterinario> searchAll();

    // Busca un veterinario por su identificador dentro del HashMap.
    Veterinario searchById(Integer id);

    // Guarda un veterinario en el HashMap asignándole automáticamente el siguiente id disponible.
    void save(Veterinario veterinario);

    // Busca un veterinario por su correo y contraseña
    Veterinario searchByCorreoYContrasena(String correo, String contrasena);
}
