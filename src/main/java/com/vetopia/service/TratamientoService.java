package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Tratamiento;

// Esta interfaz define los métodos que el servicio de tratamientos debe implementar. Los controladores interactúan con esta interfaz para realizar operaciones relacionadas con tratamientos, mientras que la implementación concreta se encarga de la lógica de negocio y delega el acceso a datos en la capa repository.
public interface TratamientoService {

    // Devuelve todos los tratamientos registrados.
    List<Tratamiento> listarTratamientos();

    // Obtiene un tratamiento validando previamente el identificador.
    Tratamiento obtenerTratamientoPorId(Integer id);

    // Guarda un tratamiento nuevo. El id lo asigna el repositorio.
    void guardar(Tratamiento tratamiento);

    // Devuelve los tratamientos asociados a un veterinario.
    List<Tratamiento> listarTratamientosPorVeterinario(Integer veterinarioId);

    // Devuelve el historial médico (tratamientos) de una mascota.
    List<Tratamiento> listarTratamientosPorMascota(Integer mascotaId);
}
