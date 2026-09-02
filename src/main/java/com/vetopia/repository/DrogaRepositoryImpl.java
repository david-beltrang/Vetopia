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

    @Override
    public Collection<Droga> searchAll() {
        return new ArrayList<>(tablaDrogas.values());
    }

    @Override
    public Droga searchById(Integer id) {
        return tablaDrogas.get(id);
    }

    @Override
    public void save(Droga droga) {
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

    @Override
    public void descontarUnidades(Integer drogaId, Integer cantidad) {
        Droga droga = tablaDrogas.get(drogaId);
        if (droga == null || cantidad == null || cantidad <= 0) {
            return;
        }
        if (droga.getUnidadesDisponibles() != null) {
            droga.setUnidadesDisponibles(Math.max(0, droga.getUnidadesDisponibles() - cantidad));
        }
        if (droga.getUnidadesVendidas() != null) {
            droga.setUnidadesVendidas(droga.getUnidadesVendidas() + cantidad);
        }
    }
}
