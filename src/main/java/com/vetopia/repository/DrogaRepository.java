package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Droga;

// Esta interfaz define los métodos que debe implementar cualquier clase que funcione como repositorio de drogas.
public interface DrogaRepository {

    // Devuelve todas las drogas registradas.
    Collection<Droga> searchAll();

    // Busca una droga por su identificador dentro del HashMap.
    Droga searchById(Integer id);

    // Guarda una droga en el HashMap asignándole automáticamente el siguiente id disponible.
    void save(Droga droga);

    // Descuenta unidades del inventario de la droga y las suma a las vendidas.
    void descontarUnidades(Integer drogaId, Integer cantidad);
}
