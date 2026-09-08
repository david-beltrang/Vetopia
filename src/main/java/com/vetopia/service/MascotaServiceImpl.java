package com.vetopia.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private ImagenService imagenService;

    @Autowired
    private DuenoRepository duenoRepository;

    @Override
    public List<Mascota> listarMascotas() {
        return mascotaRepository.findAll();
    }

    @Override
    public Mascota obtenerMascotaPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la mascota.");
        }
        Mascota m = mascotaRepository.findById(id).orElse(null);
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
    public Mascota registrarMascota(Mascota mascota, Long duenoId, MultipartFile foto) {
        if (mascota == null) {
            throw new IllegalArgumentException("No se especificó la mascota a registrar.");
        }
        if (duenoId == null) {
            throw new IllegalArgumentException("No se especificó el dueño de la mascota.");
        }
        mascota.setEstado("Activo");
        Dueno dueno = duenoRepository.findById(duenoId).orElse(null);
        if (dueno == null) {
            throw new IllegalArgumentException("No se encontró ningún dueño con el identificador " + duenoId + ".");
        }
        mascota.setDueno(dueno);
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
            Mascota actual = mascotaRepository.findById(mascota.getId()).orElse(null);
            if (actual != null) {
                if (mascota.getDueno() == null) {
                    mascota.setDueno(actual.getDueno());
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
            Mascota actual = mascotaRepository.findById(mascota.getId()).orElse(null);
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
        Mascota mascota = obtenerMascotaPorId(id);
        mascota.setEstado(estado);
        mascotaRepository.save(mascota);
    }

    @Override
    @Transactional
    public void eliminarMascota(Integer id) {
        Mascota mascota = obtenerMascotaPorId(id);
        for (Tratamiento tratamiento : mascota.getTratamientos()) {
            tratamientoService.eliminarTratamiento(tratamiento.getId());
        }
        mascotaRepository.deleteById(id);
    }

    @Override
    public List<Mascota> listarMascotasDeCliente(Long duenoId) {
        if (duenoId == null) {
            throw new IllegalArgumentException("No se especificó el identificador del dueño.");
        }
        return List.copyOf(mascotaRepository.findByDuenoId(duenoId));
    }

    @Override
    public List<Mascota> listarMascotasDeVeterinario(Integer veterinarioId) {
        if (veterinarioId == null) {
            throw new IllegalArgumentException("No se especificó el identificador del veterinario.");
        }
        List<Mascota> mascotas = new ArrayList<>();
        Set<Integer> idsYaVistos = new LinkedHashSet<>();

        for (Tratamiento tratamiento : tratamientoService.listarTratamientosPorVeterinario(veterinarioId)) {
            Mascota mascotaTratamiento = tratamiento.getMascota();
            if (mascotaTratamiento == null) {
                continue;
            }
            Integer idMascota = mascotaTratamiento.getId();
            if (idsYaVistos.add(idMascota)) {
                Mascota mascota = mascotaRepository.findById(idMascota).orElse(null);
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
    public Mascota obtenerMascotaDeCliente(Integer mascotaId, Long duenoId) {
        if (duenoId == null) {
            throw new IllegalArgumentException("No se especificó el identificador del dueño.");
        }
        Mascota mascota = obtenerMascotaPorId(mascotaId);
        if (mascota.getDueno() == null || !duenoId.equals(mascota.getDueno().getId())) {
            throw new IllegalArgumentException("No tienes permiso para ver los detalles de esta mascota.");
        }
        return mascota;
    }
}