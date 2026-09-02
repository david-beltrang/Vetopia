package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Tratamiento;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de tratamientos en la aplicación.
public interface TratamientoRepository {

    // Devuelve todos los tratamientos registrados.
    Collection<Tratamiento> searchAll();

    // Busca un tratamiento por su identificador dentro del HashMap.
    Tratamiento searchById(Integer id);

    // Guarda un tratamiento en el HashMap asignándole automáticamente el siguiente id disponible.
    void save(Tratamiento tratamiento);

    // Devuelve los tratamientos asociados a un veterinario.
    Collection<Tratamiento> searchByVeterinarioId(Integer veterinarioId);

    // Devuelve los tratamientos (historial médico) de una mascota.
    Collection<Tratamiento> searchByMascotaId(Integer mascotaId);
}
