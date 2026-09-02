package com.vetopia.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;
import com.vetopia.repository.MascotaRepository;

// Esta clase implementa la interfaz MascotaService y define la lógica de negocio relacionada con mascotas. Es el único punto al que los controladores acceden para obtener datos; a su vez, delega el acceso a datos en la capa repository.
@Service
public class MascotaServiceImpl implements MascotaService {

    //Inyección de dependencias
    @Autowired
    private MascotaRepository mascotaRepository;

    // Para saber qué mascotas atiende un veterinario se consultan sus
    // tratamientos (las mascotas aparecen una vez por cada tratamiento).
    @Autowired
    private TratamientoService tratamientoService;

    // Devuelve todas las mascotas registradas.
    @Override
    public List<Mascota> listarMascotas() {
        return List.copyOf(mascotaRepository.searchAll());
    }

    // Obtiene una mascota validando previamente el identificador.
    @Override
    public Mascota obtenerMascotaPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la mascota.");
        }
        return mascotaRepository.searchById(id);
    }

    // Guarda una mascota nueva. El id lo asigna el repositorio.
    @Override
    public void guardar(Mascota mascota) {
        mascotaRepository.save(mascota);
    }

    // Cambia el estado de una mascota de "activo" a "inactivo".
    @Override
    public void cambiarEstado(Integer id, String estado) {
        mascotaRepository.cambiarEstado(id, estado);
    }

    // Mascotas asociadas a un veterinario: se recorren sus tratamientos y
    // se agrega cada mascota una sola vez, aunque tenga varios tratamientos.
    @Override
    public List<Mascota> listarMascotasDeVeterinario(Integer veterinarioId) {
        List<Mascota> mascotas = new ArrayList<>();
        Set<Integer> idsYaVistos = new LinkedHashSet<>();

        for (Tratamiento tratamiento : tratamientoService.listarTratamientosPorVeterinario(veterinarioId)) {
            Integer idMascota = tratamiento.getMascotaId();
            if (idsYaVistos.add(idMascota)) {
                Mascota mascota = mascotaRepository.searchById(idMascota);
                if (mascota != null) {
                    mascotas.add(mascota);
                }
            }
        }
        return mascotas;
    }
}
