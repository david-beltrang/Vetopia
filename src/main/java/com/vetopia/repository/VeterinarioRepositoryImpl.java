package com.vetopia.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vetopia.entities.Veterinario;

/**
 * Simula la persistencia de veterinarios usando un HashMap
 * (llave = id, valor = Veterinario), a modo de tabla de base de datos.
 * La anotación @Repository la registra en el contexto de Spring.
 *
 * Cuando se conecte una base de datos real, solo esta implementación
 * cambia; Service y Controller no requieren modificaciones.
 */
@Repository
public class VeterinarioRepositoryImpl implements VeterinarioRepository {

    // "Tabla" veterinarios simulada en memoria
    private final Map<Integer, Veterinario> tablaVeterinarios = new HashMap<>();

    // Precarga registros de ejemplo de veterinarios
    public VeterinarioRepositoryImpl() {
        tablaVeterinarios.put(1, new Veterinario(1, "1002003001", "vet123", "veterinario@vetopia.com", "Medicina General", 0, "Carlos Gutiérrez", "Activo", "https://example.com/foto-carlos.jpg"));
        tablaVeterinarios.put(2, new Veterinario(2, "1002003002", "vet123", "laura.mendez@vetopia.com", "Cirugía", 0, "Laura Méndez", "Activo", null));
        tablaVeterinarios.put(3, new Veterinario(3, "1002003003", "vet123", "jorge.santana@vetopia.com", "Dermatología", 0, "Jorge Santana", "Inactivo", null));
    }

    // Devuelve todos los veterinarios registrados en el repositorio, convirtiendo los valores del HashMap en una lista
    @Override
    public Collection<Veterinario> searchAll() {
        return new ArrayList<>(tablaVeterinarios.values());
    }

    // Busca un veterinario por su identificador en el HashMap y devuelve el objeto Veterinario correspondiente
    @Override
    public Veterinario searchById(Integer id) {
        return tablaVeterinarios.get(id);
    }

    // Guarda el veterinario asignándole el siguiente id disponible
    @Override
    public void save(Veterinario veterinario) {
        if (veterinario.getId() == null){
            int lastId = 0;
            // Recorremos todas las llaves (IDs) existentes en la tabla
            for (int id : tablaVeterinarios.keySet()) {
                if (id > lastId) {
                    lastId = id; // Guardamos el ID más alto que encontremos
                }
            }
            // Asignamos el siguiente ID y guardamos
            veterinario.setId(lastId + 1);
            tablaVeterinarios.put(veterinario.getId(), veterinario);
            } else {
                //Si ya tiene ID, entonces se actualiza el registro
                tablaVeterinarios.put(veterinario.getId(), veterinario);
            }
    }

    // Busca un veterinario por su correo y contraseña en el HashMap y devuelve el objeto Veterinario correspondiente
    @Override
    public Veterinario searchByCorreoYContrasena(String correo, String contrasena) {
        // Utilizamos un stream para filtrar los veterinarios por correo y contraseña, devolviendo el primero que coincida o null si no se encuentra ninguno
        return tablaVeterinarios.values().stream()
                // Filtramos los veterinarios que tengan el correo y la contraseña coincidentes, ignorando mayúsculas y minúsculas en el correo
                .filter(v -> v.getCorreo() != null && v.getCorreo().equalsIgnoreCase(correo)
                        && v.getContrasena() != null && v.getContrasena().equals(contrasena))
                .findFirst()
                .orElse(null);
    }
}
