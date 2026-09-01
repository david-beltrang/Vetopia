package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Droga;

/**
 * SERVICIO - DrogaService
 * Define la lógica de negocio relacionada con drogas/medicamentos. Es el
 * único punto al que los controladores acceden para obtener datos; a su
 * vez, delega el acceso a datos en la capa repository.
 */
public interface DrogaService {

    /** Lista todas las drogas registradas. */
    List<Droga> listarDrogas();

    /** Obtiene una droga validando previamente el identificador. */
    Droga obtenerDrogaPorId(Integer id);

    /** Guarda (registra) una droga nueva. El id lo asigna el repositorio. */
    void guardar(Droga droga);

    /** Actualiza los datos de una droga existente. */
    void actualizar(Droga droga);

    /** Elimina una droga por su id. */
    void eliminar(Integer id);

    /** Descuenta unidades del stock de una droga. */
    void descontarStock(Integer id, Integer unidades);
}
