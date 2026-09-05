package com.vetopia.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vetopia.entities.Droga;

// Esta clase implementa la interfaz DrogaRepository y proporciona métodos para acceder y manipular los datos de drogas almacenados en memoria.
@Repository
public class DrogaRepositoryImpl implements DrogaRepository {

    // Se utiliza un HashMap para almacenar las drogas, donde la clave es el identificador de la droga y el valor es el objeto Droga correspondiente.
    private final Map<Integer, Droga> tablaDrogas = new HashMap<>();

    // Precarga registros de ejemplo de drogas, con sus respectivos precios y cantidades.
    public DrogaRepositoryImpl() {
        tablaDrogas.put(1, new Droga(1, "Amoxicilina", 8000.0, 15000.0, 100, 0));
        tablaDrogas.put(2, new Droga(2, "Ivermectina", 12000.0, 22000.0, 50, 0));
        tablaDrogas.put(3, new Droga(3, "Metronidazol", 6000.0, 11000.0, 75, 0));
    }

    // Metodo que devuelve todas las drogas registradas en el repositorio, convirtiendo los valores del HashMap en una lista.
    @Override
    public Collection<Droga> searchAll() {
        return new ArrayList<>(tablaDrogas.values());
    }

    // Metodo que busca una droga por su identificador en el HashMap y devuelve el objeto Droga correspondiente.
    @Override
    public Droga searchById(Integer id) {
        return tablaDrogas.get(id);
    }

    // Metodo que guarda una droga en el HashMap, asignándole automáticamente el siguiente id disponible si no tiene uno
    @Override
    public void save(Droga droga) {
        // Si la droga no tiene un ID asignado, se busca el último ID utilizado en la tabla y se le asigna el siguiente disponible
       if (droga.getId() == null){
            int lastId = 0;
            // Recorremos todas las llaves (IDs) existentes en la tabla
            for (int id : tablaDrogas.keySet()) {
                if (id > lastId) {
                    lastId = id; // Guardamos el ID más alto que encontremos
                }
            }
            // Asignamos el siguiente ID y guardamos
            droga.setId(lastId + 1);
            tablaDrogas.put(droga.getId(), droga);
            } else {
                //Si ya tiene ID, entonces se actualiza el registro
                tablaDrogas.put(droga.getId(), droga);
            }
    }

    // Metodo que descuenta unidades del inventario de la droga y las suma a las aplicadas, asegurando que no se descuente más de lo disponible
    @Override
    public void descontarUnidades(Integer drogaId, Integer cantidad) {
        Droga droga = tablaDrogas.get(drogaId);
        // Si la droga no existe o la cantidad a descontar es nula o menor o igual a cero, no se realiza ninguna acción
        if (droga == null || cantidad == null || cantidad <= 0) {
            return;
        }
        // Se descuenta la cantidad de unidades disponibles, asegurando que no se descuente más de lo que hay disponible
        if (droga.getUnidadesDisponibles() != null) {
            droga.setUnidadesDisponibles(Math.max(0, droga.getUnidadesDisponibles() - cantidad));
        }
        // Se suma la cantidad de unidades vendidas, asegurando que no se sume más de lo que hay disponible
        if (droga.getUnidadesVendidas() != null) {
            droga.setUnidadesVendidas(droga.getUnidadesVendidas() + cantidad);
        }
    }
}
