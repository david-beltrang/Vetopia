package com.vetopia.service.mascota;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.vetopia.entities.Dueno;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.exception.RecursoNoEncontradoException;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.service.ImagenService;
import com.vetopia.service.dueno.DuenoService;
import com.vetopia.service.tratamiento.TratamientoService;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private DuenoService duenoService;

    @Autowired
    private ImagenService imagenService;

    @Override
    public List<Mascota> listarMascotas() {
        return mascotaRepository.findAll();
    }

    @Override
    public Mascota obtenerMascotaPorId(Long id) {
        if (id == null) {
            throw new ParametroInvalidoException("No se especificó el identificador de la mascota.");
        }
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró ninguna mascota con el identificador " + id + "."));
    }

    @Override
    public void guardar(Mascota mascota) {
        mascotaRepository.save(mascota);
    }

    @Override
    @Transactional
    public Mascota registrarMascota(Mascota mascota, Long duenoId, MultipartFile foto) {
        if (mascota == null) {
            throw new ParametroInvalidoException("No se especificó la mascota a registrar.");
        }
        if (duenoId == null) {
            throw new ParametroInvalidoException("No se especificó el dueño de la mascota.");
        }
        // La relación n-1 se establece con la entidad, no con un id suelto.
        Dueno dueno = duenoService.obtenerDuenoPorId(duenoId);
        mascota.setDueno(dueno);
        mascota.setEstado(DominioEstado.ACTIVO);
        // Datos obligatorios de la mascota.
        if (mascota.getColor() == null || mascota.getColor().isBlank()) {
            throw new ParametroInvalidoException("Debe indicar el color de la mascota.");
        }
        if (mascota.getFechaIngreso() == null) {
            throw new ParametroInvalidoException("Debe indicar la fecha de ingreso de la mascota.");
        }
        String imagen = imagenService.guardarFoto(foto);
        if (imagen != null) {
            mascota.setImagen(imagen);
        }
        mascotaRepository.save(mascota);
        return mascota;
    }

    @Override
    @Transactional
    public Mascota actualizarMascota(Mascota mascota, MultipartFile foto) {
        if (mascota == null) {
            throw new ParametroInvalidoException("No se especificó la mascota a actualizar.");
        }
        if (mascota.getColor() == null || mascota.getColor().isBlank()) {
            throw new ParametroInvalidoException("Debe indicar el color de la mascota.");
        }
        if (mascota.getFechaIngreso() == null) {
            throw new ParametroInvalidoException("Debe indicar la fecha de ingreso de la mascota.");
        }
        if (mascota.getId() != null) {
            Mascota actual = mascotaRepository.findById(mascota.getId()).orElse(null);
            if (actual != null) {
                // Se conservan la relación con el dueño y el estado si no vinieron
                // en el formulario de edición.
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
    @Transactional
    public void cambiarEstado(Long id, String estado) {
        if (id == null) {
            throw new ParametroInvalidoException("No se especificó el identificador de la mascota.");
        }
        if (estado == null || estado.isBlank()) {
            throw new ParametroInvalidoException("No se especificó el estado de la mascota.");
        }
        Mascota mascota = obtenerMascotaPorId(id);
        mascota.setEstado(DominioEstado.fromValor(estado));
        mascotaRepository.save(mascota);
    }

    @Override
    public List<Mascota> listarMascotasDeCliente(Long duenoId) {
        if (duenoId == null) {
            throw new ParametroInvalidoException("No se especificó el identificador del dueño.");
        }
        return mascotaRepository.findByDuenoId(duenoId);
    }

    // Orquestación en la capa SERVICE: consulta los tratamientos del veterinario
    // (TratamientoRepository) y con cada uno obtiene su mascota (MascotaRepository),
    // evitando repetir mascotas atendidas.
    @Override
    public List<Mascota> listarMascotasDeVeterinario(Long veterinarioId) {
        if (veterinarioId == null) {
            throw new ParametroInvalidoException("No se especificó el identificador del veterinario.");
        }
        List<Mascota> mascotas = new ArrayList<>();
        Set<Long> idsYaVistos = new LinkedHashSet<>();

        for (Tratamiento tratamiento : tratamientoService.listarTratamientosPorVeterinario(veterinarioId)) {
            Mascota mascota = tratamiento.getMascota();
            Long idMascota = mascota == null ? null : mascota.getId();
            if (idMascota != null && idsYaVistos.add(idMascota)) {
                mascotas.add(mascota);
            } else if (mascota == null) {
                throw new RecursoNoEncontradoException(
                        "No se encontró la mascota asociada a un tratamiento.");
            }
        }
        return mascotas;
    }

    @Override
    public Mascota obtenerMascotaDeCliente(Long mascotaId, Long duenoId) {
        if (duenoId == null) {
            throw new ParametroInvalidoException("No se especificó el identificador del dueño.");
        }
        Mascota mascota = obtenerMascotaPorId(mascotaId);
        Dueno dueno = mascota.getDueno();
        if (dueno == null || !duenoId.equals(dueno.getId())) {
            throw new ParametroInvalidoException("No tienes permiso para ver los detalles de esta mascota.");
        }
        return mascota;
    }

    // Busca mascotas por nombre, parcial e insensible a mayúsculas.
    @Override
    public List<Mascota> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ParametroInvalidoException("Debe indicar el nombre de la mascota a buscar.");
        }
        return mascotaRepository.findByNombreContainingIgnoreCase(nombre.trim());
    }
}