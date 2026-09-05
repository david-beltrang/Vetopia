package com.vetopia.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Repository;

import com.vetopia.entities.Tratamiento;

// Esta clase implementa la interfaz TratamientoRepository y proporciona métodos para acceder y manipular los datos de tratamientos en la aplicación.
@Repository
public class TratamientoRepositoryImpl implements TratamientoRepository {

    // Se utiliza un HashMap para almacenar los tratamientos, donde la clave es el identificador del tratamiento y el valor es el objeto Tratamiento correspondiente.
    private final Map<Integer, Tratamiento> tablaTratamientos = new HashMap<>();

    // Precarga de registros de ejemplo de tratamientos, con sus respectivos datos.
    // Cada tratamiento lleva además el título, la descripción y si está completado,
    // que es lo que se muestra en el historial de la ficha clínica.
    public TratamientoRepositoryImpl() {
        tablaTratamientos.put(1, new Tratamiento(1, LocalDate.parse("2026-08-10"), 2, 1, 1,
                "Extracción de cuerpo extraño", "Procedimiento sin complicaciones. Recuperación satisfactoria.", true));
        tablaTratamientos.put(2, new Tratamiento(2, LocalDate.parse("2026-08-15"), 1, 1, 2,
                "Vacunación anual", "Refuerzo múltiple y rabia. Próxima dosis en un año.", true));
        tablaTratamientos.put(3, new Tratamiento(3, LocalDate.parse("2026-08-20"), 2, 1, 2,
                "Control por dermatitis", "Aplicación de tratamiento. Pendiente revisión en 10 días.", false));
        tablaTratamientos.put(4, new Tratamiento(4, LocalDate.parse("2026-08-25"), 3, 2, 3,
                "Consulta general", "Evaluación de rutina sin hallazgos.", true));
    }

    // Metodo que devuelve todos los tratamientos registrados en el repositorio, convirtiendo los valores del HashMap en una lista
    @Override
    public Collection<Tratamiento> searchAll() {
        return new ArrayList<>(tablaTratamientos.values());
    }

    // Metodo que busca un tratamiento por su identificador en el HashMap y devuelve el objeto Tratamiento correspondiente
    @Override
    public Tratamiento searchById(Integer id) {
        return tablaTratamientos.get(id);
    }

    // Guarda el tratamiento asignándole el siguiente id disponible.
    @Override
    public void save(Tratamiento tratamiento) {
        if (tratamiento.getId() == null){
            int lastId = 0;
            // Recorremos todas las llaves (IDs) existentes en la tabla
            for (int id : tablaTratamientos.keySet()) {
                if (id > lastId) {
                    lastId = id; // Guardamos el ID más alto que encontremos
                }
            }
            // Asignamos el siguiente ID y guardamos
            tratamiento.setId(lastId + 1);
            tablaTratamientos.put(tratamiento.getId(), tratamiento);
            } else {
                //Si ya tiene ID, entonces se actualiza el registro
                tablaTratamientos.put(tratamiento.getId(), tratamiento);
            }
    }

    // Devuelve solo los tratamientos cuyo veterinario sea el recibido.
    @Override
    public Collection<Tratamiento> searchByVeterinarioId(Integer veterinarioId) {
        Collection<Tratamiento> resultado = new ArrayList<>();
        for (Tratamiento tratamiento : tablaTratamientos.values()) {
            if (Objects.equals(tratamiento.getVeterinarioId(), veterinarioId)) {
                resultado.add(tratamiento);
            }
        }
        return resultado;
    }

    // Devuelve los tratamientos (historial médico) de una mascota concreta.
    @Override
    public Collection<Tratamiento> searchByMascotaId(Integer mascotaId) {
        Collection<Tratamiento> resultado = new ArrayList<>();
        // Recorremos todas los tratamientos en el HashMap y agregamos a la lista resultado aquellos que tengan el mismo mascotaId que el recibido
        for (Tratamiento tratamiento : tablaTratamientos.values()) {
            if (Objects.equals(tratamiento.getMascotaId(), mascotaId)) {
                resultado.add(tratamiento);
            }
        }
        return resultado;
    }
}
