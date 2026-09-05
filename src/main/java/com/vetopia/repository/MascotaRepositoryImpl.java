package com.vetopia.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Repository;

import com.vetopia.entities.Mascota;

// Esta clase implementa la interfaz MascotaRepository y proporciona métodos para acceder y manipular los datos de mascotas almacenados en memoria.
@Repository
public class MascotaRepositoryImpl implements MascotaRepository {

    // Se utiliza un HashMap para almacenar las mascotas, donde la clave es el identificador de la mascota y el valor es el objeto Mascota correspondiente.
    private final Map<Integer, Mascota> tablaMascotas = new HashMap<>();

    //precarga de registros
    public MascotaRepositoryImpl() {
        tablaMascotas.put(1, new Mascota(1, "Max", "Perro", "Doberman",
                5.0, "Macho", "https://imgs.search.brave.com/8AIQQXIiiwAhhRKhD87lE0EUhLc7Irg8eiwpgIx1x7U/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4u/cGl4YWJheS5jb20v/cGhvdG8vMjAxNi8w/My8yNy8xOC8xMi9s/dW5hLTEyODMzNTZf/NjQwLmpwZw", 32.0, "Negro", LocalDate.parse("2026-01-12"), 1, "Activo"));
        tablaMascotas.put(2, new Mascota(2, "Luna", "Gato", "Siamés",
                3.0, "Hembra", "https://imgs.search.brave.com/lyQdUI6O4tK7o734zlUOxuFLdw-1kUFmZssTDwlkam0/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5pc3RvY2twaG90/by5jb20vaWQvMTU0/MjEyMDY2L2VzL2Zv/dG8vZ2F0by1zaWFt/JUMzJUE5cy5qcGc_/cz02MTJ4NjEyJnc9/MCZrPTIwJmM9ZV9V/WDktSU5fZ3VPQWhw/bUxVYk40VGktWVBj/LU92c3hWQXJUOWpn/MWxVWT0", 4.2, "Blanco y café", LocalDate.parse("2026-02-03"), 2, "Activo"));
        tablaMascotas.put(3, new Mascota(3, "Rocky", "Perro", "Bulldog Francés",
                0.6, "Macho", "https://imgs.search.brave.com/SpvOGsBrp_Lq8prflOD7fsZ5TRntmOqrUHvFvtSrWzg/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90aHVt/YnMuZHJlYW1zdGlt/ZS5jb20vYi9zZW50/YWRhLWdyaXMtZGVs/LXBlcnJpdG8tZG9n/by1mcmFuYyVDMyVB/OXMtYWlzbGFkYS0x/MDE3NTU1MzAuanBn", 7.5, "Gris atigrado", LocalDate.parse("2025-11-20"), 3, "Activo"));
    }

    // Metodo que devuelve todas las mascotas registradas en el repositorio, convirtiendo los valores del HashMap en una lista
    @Override
    public Collection<Mascota> searchAll() {
        return new ArrayList<>(tablaMascotas.values());
    }

    // Metodo que busca una mascota por su identificador en el HashMap y devuelve el objeto Mascota correspondiente
    @Override
    public Mascota searchById(Integer id) {
        return tablaMascotas.get(id);
    }

    // Guarda la mascota asignándole el siguiente id disponible
    @Override
    public void save(Mascota mascota) {
        // Si la mascota no tiene un ID asignado, se busca el último ID utilizado en la tabla y se le asigna el siguiente disponible
        if (mascota.getId() == null){
            int lastId = 0;
            // Recorremos todas las llaves (IDs) existentes en la tabla
            for (int id : tablaMascotas.keySet()) {
                if (id > lastId) {
                    lastId = id; // Guardamos el ID más alto que encontremos
                }
            }
            // Asignamos el siguiente ID y guardamos
            mascota.setId(lastId + 1);
            tablaMascotas.put(mascota.getId(), mascota);
            } else {
                //Si ya tiene ID, entonces se actualiza el registro
                tablaMascotas.put(mascota.getId(), mascota);
            }
    }

    // Cambia el estado de la mascota con el id especificado.
    @Override
    public void cambiarEstado(Integer id, String estado) {
        Mascota mascota = tablaMascotas.get(id);
        mascota.setEstado(estado);
    }

    // Elimina la mascota con el id especificado.
    @Override
    public boolean eliminar(Integer id) {
        return tablaMascotas.remove(id) != null;
    }

    // Devuelve solo las mascotas cuyo dueño sea el recibido.
    @Override
    public Collection<Mascota> searchByDuenoId(Integer duenoId) {
        Collection<Mascota> resultado = new ArrayList<>();
        // Recorremos todas las mascotas en el HashMap y agregamos a la lista resultado aquellas que tengan el mismo duenoId que el recibido
        for (Mascota mascota : tablaMascotas.values()) {
            if (Objects.equals(mascota.getDuenoId(), duenoId)) {
                resultado.add(mascota);
            }
        }
        return resultado;
    }
}
