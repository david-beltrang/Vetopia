package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Mascota;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de mascotas en la aplicación.
public interface MascotaRepository {

    // Devuelve todas las mascotas registradas.
    Collection<Mascota> searchAll();

    // Busca una mascota por su identificador dentro del HashMap.
    Mascota searchById(Integer id);

    // Guarda una mascota en el HashMap asignándole automáticamente el siguiente id disponible.
    void save(Mascota mascota);

    void cambiarEstado(Integer id, String estado);
}
