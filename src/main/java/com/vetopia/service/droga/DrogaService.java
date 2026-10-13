package com.vetopia.service.droga;

import java.util.List;

import com.vetopia.entities.Droga;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de drogas en la aplicación
public interface DrogaService {

    // Lista todas las drogas registradas
    List<Droga> listarDrogas();

    // Obtiene una droga validando previamente el identificador
    Droga obtenerDrogaPorId(Long id);

    // Guarda (registra) una droga nueva. El id lo asigna la base de datos
    void guardar(Droga droga);

    // Descuenta unidades del inventario de la droga
    void descontarUnidades(Long drogaId, Integer cantidad);

    // Elimina una droga por su id
    void eliminarDroga(Long id);
}