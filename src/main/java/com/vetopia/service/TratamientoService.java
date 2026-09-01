package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Tratamiento;

/**
 * SERVICIO - TratamientoService
 * Define la lógica de negocio relacionada con tratamientos. Es el único
 * punto al que los controladores acceden para obtener datos; a su vez,
 * delega el acceso a datos en la capa repository.
 */
public interface TratamientoService {

    /** Lista todos los tratamientos registrados. */
    List<Tratamiento> listarTratamientos();

    /** Obtiene un tratamiento validando previamente el identificador. */
    Tratamiento obtenerTratamientoPorId(Integer id);

    /** Guarda (registra) un tratamiento nuevo. El id lo asigna el repositorio. */
    void guardar(Tratamiento tratamiento);

    /** Lista los tratamientos de una mascota específica. */
    List<Tratamiento> listarTratamientosPorMascotaId(Integer mascotaId);

    /** Actualiza un tratamiento existente. */
    void actualizar(Tratamiento tratamiento);

    /** Elimina un tratamiento por su id. */
    void eliminar(Integer id);
}
