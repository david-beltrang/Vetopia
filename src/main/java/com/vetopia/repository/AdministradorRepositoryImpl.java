package com.vetopia.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vetopia.entities.Administrador;

/**
 * Simula la persistencia de administradores usando un HashMap
 * (llave = id, valor = Administrador), a modo de tabla de base de datos.
 * La anotación @Repository la registra en el contexto de Spring.
 *
 */
@Repository
public class AdministradorRepositoryImpl implements AdministradorRepository {

    // "Tabla" administradores simulada en memoria
    private final Map<Integer, Administrador> tablaAdministradores = new HashMap<>();

    // Precarga registros de ejemplo de administradores
    public AdministradorRepositoryImpl() {
        tablaAdministradores.put(1, new Administrador(1, "1000000001", "admin@vetopia.com", "admin123", "Dirección Vetopia"));
        tablaAdministradores.put(2, new Administrador(2, "1000000002", "gerencia@vetopia.com", "gerencia123", "Gerencia General"));
        tablaAdministradores.put(3, new Administrador(3, "1000000003", "finanzas@vetopia.com", "finanzas123", "Finanzas"));
    }

    // Metodo para buscar todos los administradores
    @Override
    public Collection<Administrador> searchAll() {
        return new ArrayList<>(tablaAdministradores.values());
    }

    // Metodo para buscar un administrador por su ID
    @Override
    public Administrador searchById(Integer id) {
        return tablaAdministradores.get(id);
    }

    // Metodo para guardar un administrador en la "tabla"
    @Override
    public void save(Administrador administrador) {
        if (administrador.getId() == null){
            int lastId = 0;
            // Recorremos todas las llaves (IDs) existentes en la tabla
            for (int id : tablaAdministradores.keySet()) {
                // Guardamos el ID más alto que encontremos
                if (id > lastId) {
                    lastId = id;
                }
            }
            // Asignamos el siguiente ID y guardamos
            administrador.setId(lastId + 1);
            tablaAdministradores.put(administrador.getId(), administrador);
            } else {
                //Si ya tiene ID, entonces se actualiza el registro
                tablaAdministradores.put(administrador.getId(), administrador);
            }
    }

    // Metodo para buscar un administrador por su correo y contraseña
    @Override
    public Administrador searchByCorreoYContrasena(String correo, String contrasena) {
        // Filtramos la colección de administradores para encontrar uno que coincida con el correo y la contraseña proporcionados
        return tablaAdministradores.values().stream()
                .filter(a -> a.getCorreo() != null && a.getCorreo().equalsIgnoreCase(correo)
                        && a.getContrasena() != null && a.getContrasena().equals(contrasena))
                        // Retornamos el primer administrador que coincida, o null si no se encuentra ninguno
                .findFirst()
                .orElse(null);
    }
}
