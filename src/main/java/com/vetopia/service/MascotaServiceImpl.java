package com.vetopia.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;
import com.vetopia.repository.MascotaRepository;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private ImagenService imagenService;

    @Override
    public List<Mascota> listarMascotas() {
        Collection<Mascota> mascotas = mascotaRepository.searchAll();
        return mascotas == null ? List.of() : List.copyOf(mascotas);
    }

    @Override
    public Mascota obtenerMascotaPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la mascota.");
        }
        Mascota m = mascotaRepository.searchById(id);
        if (m == null) {
            throw new IllegalArgumentException("No se encontró ninguna mascota con el identificador " + id + ".");
        }
        return m;
    }

    @Override
    public void guardar(Mascota mascota) {
        mascotaRepository.save(mascota);
    }

    @Override
    public Mascota registrarMascota(Mascota mascota, Integer duenoId, MultipartFile foto) {
        if (mascota == null) {
            throw new IllegalArgumentException("No se especificó la mascota a registrar.");
        }
        if (duenoId == null) {
            throw new IllegalArgumentException("No se especificó el dueño de la mascota.");
        }
        mascota.setEstado("Activo");
        mascota.setDuenoId(duenoId);
        String imagen = imagenService.guardarFoto(foto);
        if (imagen != null) {
            mascota.setImagen(imagen);
        }
        mascotaRepository.save(mascota);
        return mascota;
    }

    @Override
    public Mascota actualizarMascota(Mascota mascota, MultipartFile foto) {
        if (mascota == null) {
            throw new IllegalArgumentException("No se especificó la mascota a actualizar.");
        }
        if (mascota.getId() != null) {
            Mascota actual = mascotaRepository.searchById(mascota.getId());
            if (actual != null) {
                if (mascota.getDuenoId() == null) {
                    mascota.setDuenoId(actual.getDuenoId());
                }
                if (mascota.getEstado() == null) {
                    mascota.setEstado(actual.getEstado());
                }
            }
        }
        String imagen = imagenService.guardarFoto(foto);
        if (imagen != null) {
            mascota.setImagen(imagen);
        } else if (mascota.getImagen() == null && mascota.getId() != null) {
            Mascota actual = mascotaRepository.searchById(mascota.getId());
            if (actual != null) {
                mascota.setImagen(actual.getImagen());
            }
        }
        mascotaRepository.save(mascota);
        return mascota;
    }

    @Override
    public void cambiarEstado(Integer id, String estado) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la mascota.");
        }
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("No se especificó el estado de la mascota.");
        }
        mascotaRepository.cambiarEstado(id, estado);
    }

    @Override
    public void eliminarMascota(Integer id) {
        obtenerMascotaPorId(id);
        tratamientoService.eliminarTratamientosDeMascota(id);
        mascotaRepository.eliminar(id);
    }

    @Override
    public List<Mascota> listarMascotasDeCliente(Integer duenoId) {
        if (duenoId == null) {
            throw new IllegalArgumentException("No se especificó el identificador del dueño.");
        }
        return List.copyOf(mascotaRepository.searchByDuenoId(duenoId));
    }

    @Override
    public List<Mascota> listarMascotasDeVeterinario(Integer veterinarioId) {
        if (veterinarioId == null) {
            throw new IllegalArgumentException("No se especificó el identificador del veterinario.");
        }
        List<Mascota> mascotas = new ArrayList<>();
        Set<Integer> idsYaVistos = new LinkedHashSet<>();

        for (Tratamiento tratamiento : tratamientoService.listarTratamientosPorVeterinario(veterinarioId)) {
            Integer idMascota = tratamiento.getMascotaId();
            if (idsYaVistos.add(idMascota)) {
                Mascota mascota = mascotaRepository.searchById(idMascota);
                if (mascota != null) {
                    mascotas.add(mascota);
                } else {
                    throw new IllegalArgumentException(
                            "No se encontró ninguna mascota con el identificador " + idMascota + ".");
                }
            }
        }
        return mascotas;
    }

    @Override
    public Mascota obtenerMascotaDeCliente(Integer mascotaId, Integer duenoId) {
        if (duenoId == null) {
            throw new IllegalArgumentException("No se especificó el identificador del dueño.");
        }
        Mascota mascota = obtenerMascotaPorId(mascotaId);
        if (!duenoId.equals(mascota.getDuenoId())) {
            throw new IllegalArgumentException("No tienes permiso para ver los detalles de esta mascota.");
        }
        return mascota;
    }
}