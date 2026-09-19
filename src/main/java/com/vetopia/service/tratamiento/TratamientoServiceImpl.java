package com.vetopia.service.tratamiento;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.exception.RecursoNoEncontradoException;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.TratamientoRepository;
import com.vetopia.service.droga.DrogaService;
import com.vetopia.service.veterinario.VeterinarioService;

@Service
public class TratamientoServiceImpl implements TratamientoService {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private DrogaService drogaService;

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Override
    public List<Tratamiento> listarTratamientos() {
        return tratamientoRepository.findAll();
    }

    @Override
    public Tratamiento obtenerTratamientoPorId(Long id) {
        if (id == null) {
            throw new ParametroInvalidoException("No se especificó el identificador del tratamiento.");
        }
        return tratamientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró ningún tratamiento con el identificador " + id + "."));
    }

    @Override
    public void guardar(Tratamiento tratamiento) {
        tratamientoRepository.save(tratamiento);
    }

    @Override
    public List<Tratamiento> listarTratamientosPorVeterinario(Long veterinarioId) {
        if (veterinarioId == null) {
            throw new ParametroInvalidoException("No se especificó el identificador del veterinario.");
        }
        return tratamientoRepository.findByVeterinarioId(veterinarioId);
    }

    @Override
    public List<Tratamiento> listarTratamientosPorMascota(Long mascotaId) {
        if (mascotaId == null) {
            throw new ParametroInvalidoException("No se especificó el identificador de la mascota.");
        }
        return tratamientoRepository.findByMascotaId(mascotaId);
    }

    // La droga del tratamiento se navega directamente con
    // tratamiento.getDroga().getNombre() en lugar de buscar por id.
    private String resolverNombreDroga(Tratamiento tratamiento) {
        if (tratamiento.getDroga() != null && tratamiento.getDroga().getNombre() != null) {
            return tratamiento.getDroga().getNombre();
        }
        return "Medicamento";
    }

    @Override
    public List<HistorialTratamientoMascota> listarTratamientosPorVeterinarioConDroga(Long veterinarioId) {
        List<HistorialTratamientoMascota> items = new ArrayList<>();
        for (Tratamiento tratamiento : listarTratamientosPorVeterinario(veterinarioId)) {
            items.add(new HistorialTratamientoMascota(tratamiento, resolverNombreDroga(tratamiento)));
        }
        return items;
    }

    @Override
    public List<HistorialTratamientoMascota> listarHistorialDeMascota(Long mascotaId) {
        List<HistorialTratamientoMascota> historial = new ArrayList<>();
        for (Tratamiento tratamiento : listarTratamientosPorMascota(mascotaId)) {
            historial.add(new HistorialTratamientoMascota(tratamiento, resolverNombreDroga(tratamiento)));
        }
        return historial;
    }

    // Hace varias operaciones sobre la BD comoguardar el tratamiento y descontar el
    // inventario de la droga), por lo que va en una única transacción.
    @Override
    @Transactional
    public ResultadoTratamiento asignarTratamiento(Tratamiento tratamiento, Long veterinarioId) {
        if (tratamiento == null) {
            throw new ParametroInvalidoException("No se especificó el tratamiento a asignar.");
        }
        if (tratamiento.getMascota() == null || tratamiento.getMascota().getId() == null
                || tratamiento.getDroga() == null || tratamiento.getDroga().getId() == null) {
            throw new ParametroInvalidoException("Debes seleccionar un paciente y un medicamento para asignar el tratamiento.");
        }

        Droga droga = drogaService.obtenerDrogaPorId(tratamiento.getDroga().getId());

        int unidadesDisponibles = droga.getUnidadesDisponibles() == null ? 0 : droga.getUnidadesDisponibles();
        if (unidadesDisponibles <= 0) {
            throw new ParametroInvalidoException(
                    "El medicamento " + droga.getNombre() + " no tiene unidades disponibles.");
        }

        Long idVeterinario = veterinarioId != null ? veterinarioId
                : tratamiento.getVeterinario() != null ? tratamiento.getVeterinario().getId() : null;
        if (idVeterinario == null) {
            throw new ParametroInvalidoException("No se identificó al veterinario que aplica el tratamiento.");
        }
        // La relación con el veterinario se establece con la entidad.
        Veterinario veterinario = veterinarioService.obtenerVeterinarioPorId(idVeterinario);
        tratamiento.setVeterinario(veterinario);
        // Un tratamiento recién asignado parte sin completar.
        if (tratamiento.getCompletado() == null) {
            tratamiento.setCompletado(false);
        }

        guardar(tratamiento);
        drogaService.descontarUnidades(tratamiento.getDroga().getId(), 1);

        Mascota mascota = mascotaRepository.findById(tratamiento.getMascota().getId()).orElse(null);
        if (mascota == null) {
            throw new ParametroInvalidoException("El paciente seleccionado no existe.");
        }

        if (mascota.getEstado() != DominioEstado.ACTIVO) {
            mascota.setEstado(DominioEstado.ACTIVO);
            mascotaRepository.save(mascota);
        }
        return new ResultadoTratamiento(tratamiento, mascota, droga, 1);
    }
}