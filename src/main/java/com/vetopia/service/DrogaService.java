package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Droga;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de drogas en la aplicación.
public interface DrogaService {

    // Lista todas las drogas registradas.
    List<Droga> listarDrogas();

    // Obtiene una droga validando previamente el identificador.
    Droga obtenerDrogaPorId(Long id);

    // Guarda (registra) una droga nueva. El id lo asigna el repositorio.
    void guardar(Droga droga);

    // Descuenta unidades del inventario de la droga (al asignar un tratamiento).
    void descontarUnidades(Long drogaId, Integer cantidad);
}
