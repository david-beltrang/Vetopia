package com.vetopia.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vetopia.entities.Dueno;

// Esta clase implementa la interfaz DuenoRepository y proporciona métodos para acceder y manipular los datos de dueños almacenados en memoria.
@Repository
public class DuenoRepositoryImpl implements DuenoRepository {

    // Se utiliza un HashMap para almacenar los dueños, donde la clave es el identificador del dueño y el valor es el objeto Dueno correspondiente.
    private final Map<Integer, Dueno> tablaDuenos = new HashMap<>();

    // Precarga de registros de ejemplo de dueños, con sus respectivos datos.
    public DuenoRepositoryImpl() {
        tablaDuenos.put(1, new Dueno(1, "1020304050", "Ana Rodríguez", "ana@correo.com", "dueño123", "3001234567", "Activo"));
        tablaDuenos.put(2, new Dueno(2, "1020304051", "Pedro Gómez", "pedro@correo.com", "dueño123", "3007654321", "Activo"));
        tablaDuenos.put(3, new Dueno(3, "1020304052", "María López", "maria@correo.com", "dueño123", "3010001111", "Inactivo"));
    }

    @Override
    public Collection<Dueno> searchAll() {
        return new ArrayList<>(tablaDuenos.values());
    }

    @Override
    public Dueno searchById(Integer id) {
        return tablaDuenos.get(id);
    }

    // Guarda el dueño asignándole el siguiente id disponible.
    @Override
    public void save(Dueno dueno) {
        if (dueno.getId() == null){
            int lastId = 0;
            // Recorremos todas las llaves (IDs) existentes en la tabla
            for (int id : tablaDuenos.keySet()) {
                if (id > lastId) {
                    lastId = id; // Guardamos el ID más alto que encontremos
                }
            }
            // Asignamos el siguiente ID y guardamos
            dueno.setId(lastId + 1);
            tablaDuenos.put(dueno.getId(), dueno);
            } else {
                //Si ya tiene ID, entonces se actualiza el registro
                tablaDuenos.put(dueno.getId(), dueno);
            }
    }

    @Override
    public Dueno searchByCorreoYContrasena(String correo, String contrasena) {
        return tablaDuenos.values().stream()
                .filter(d -> d.getCorreo() != null && d.getCorreo().equalsIgnoreCase(correo)
                        && d.getContrasena() != null && d.getContrasena().equals(contrasena))
                .findFirst()
                .orElse(null);
    }
}