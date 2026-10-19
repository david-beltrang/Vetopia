package com.vetopia.service.tratamiento;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vetopia.dto.HistorialTratamientoDTO;
import com.vetopia.dto.ResultadoTratamientoDTO;
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

    // Lista todos los tratamientos registrados en la base de datos
    @Override
    public List<Tratamiento> listarTratamientos() {
        return tratamientoRepository.findAll();
    }

    // Obtiene un tratamiento por su identificador
    @Override
    public Tratamiento obtenerTratamientoPorId(Long id) {
        if (id == null) {
            throw new ParametroInvalidoException("No se especificó el identificador del tratamiento.");
        }
        return tratamientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró ningún tratamiento con el identificador " + id + "."));
    }

    // Guarda (registra) un tratamiento nuevo. El id lo asigna la base de datos
    @Override
    public void guardar(Tratamiento tratamiento) {
        tratamientoRepository.save(tratamiento);
    }

    // Lista los tratamientos asociados a un veterinario específico
    @Override
    public List<Tratamiento> listarTratamientosPorVeterinario(Long veterinarioId) {
        if (veterinarioId == null) {
            throw new ParametroInvalidoException("No se especificó el identificador del veterinario.");
        }
        return tratamientoRepository.findByVeterinarioId(veterinarioId);
    }

    // Lista los tratamientos asociados a una mascota específica
    @Override
    public List<Tratamiento> listarTratamientosPorMascota(Long mascotaId) {
        if (mascotaId == null) {
            throw new ParametroInvalidoException("No se especificó el identificador de la mascota.");
        }
        return tratamientoRepository.findByMascotaId(mascotaId);
    }

    // La droga del tratamiento se navega directamente con
    // tratamiento.getDroga().getNombre() en lugar de buscar por id
    private String resolverNombreDroga(Tratamiento tratamiento) {
        if (tratamiento.getDroga() != null && tratamiento.getDroga().getNombre() != null) {
            return tratamiento.getDroga().getNombre();
        }
        return "Medicamento";
    }

    // El nombre del cliente se navega con la relación de la mascota
    // (tratamiento -> mascota -> dueno). Alimenta el DTO del listado del
    // veterinario para no duplicar la entidad Dueno completa en el JSON.
    private String resolverNombreCliente(Tratamiento tratamiento) {
        Mascota mascota = tratamiento.getMascota();
        if (mascota != null && mascota.getDueno() != null && mascota.getDueno().getNombre() != null) {
            return mascota.getDueno().getNombre();
        }
        return "No registrado";
    }

    // Lista los tratamientos asociados a un veterinario específico, incluyendo el nombre de la droga utilizada
    @Override
    public List<HistorialTratamientoDTO> listarTratamientosPorVeterinarioConDroga(Long veterinarioId) {
        List<HistorialTratamientoDTO> items = new ArrayList<>();
        for (Tratamiento tratamiento : listarTratamientosPorVeterinario(veterinarioId)) {
            items.add(new HistorialTratamientoDTO(tratamiento, resolverNombreDroga(tratamiento),
                    resolverNombreCliente(tratamiento)));
        }
        return items;
    }

    // Lista los tratamientos asociados a una mascota específica, incluyendo el nombre de la droga utilizada
    @Override
    public List<HistorialTratamientoDTO> listarHistorialDeMascota(Long mascotaId) {
        List<HistorialTratamientoDTO> historial = new ArrayList<>();
        for (Tratamiento tratamiento : listarTratamientosPorMascota(mascotaId)) {
            historial.add(new HistorialTratamientoDTO(tratamiento, resolverNombreDroga(tratamiento),
                    resolverNombreCliente(tratamiento)));
        }
        return historial;
    }

    // Hace varias operaciones sobre la BD comoguardar el tratamiento y descontar el
    // inventario de la droga), por lo que va en una única transacción
    @Override
    @Transactional
    public ResultadoTratamientoDTO asignarTratamiento(Tratamiento tratamiento, Long veterinarioId) {   
        //  Validamos que se haya especificado el tratamiento a asignar
        if (tratamiento == null) {
            throw new ParametroInvalidoException("No se especificó el tratamiento a asignar.");
        }
        // Validamos que se haya seleccionado un paciente y un medicamento para asignar el tratamiento
        if (tratamiento.getMascota() == null || tratamiento.getMascota().getId() == null
                || tratamiento.getDroga() == null || tratamiento.getDroga().getId() == null) {
            throw new ParametroInvalidoException("Debes seleccionar un paciente y un medicamento para asignar el tratamiento.");
        }

        Droga droga = drogaService.obtenerDrogaPorId(tratamiento.getDroga().getId());

        int unidadesDisponibles = droga.getUnidadesDisponibles() == null ? 0 : droga.getUnidadesDisponibles();
        // Validamos que haya unidades disponibles del medicamento antes de asignar el tratamiento
        if (unidadesDisponibles <= 0) {
            throw new ParametroInvalidoException(
                    "El medicamento " + droga.getNombre() + " no tiene unidades disponibles.");
        }

        Long idVeterinario = veterinarioId != null ? veterinarioId
                : tratamiento.getVeterinario() != null ? tratamiento.getVeterinario().getId() : null;
        // Validamos que se haya especificado el veterinario que aplicará el tratamiento
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
        // Validamos que la mascota asociada al tratamiento exista en la base de datos
        if (mascota == null) {
            throw new ParametroInvalidoException("El paciente seleccionado no existe.");
        }

        // Si la mascota estaba inactiva, la activamos al aplicarle un tratamiento
        if (mascota.getEstado() != DominioEstado.ACTIVO) {
            mascota.setEstado(DominioEstado.ACTIVO);
            mascotaRepository.save(mascota);
        }
        return new ResultadoTratamientoDTO(tratamiento, mascota, droga, 1);
    }

    @Override
    @Transactional
    public void marcarTratamientoComoCompletado(Long id) {
        actualizarEstado(id, true);
    }

    // Alterna el estado del tratamiento: el veterinario puede marcarlo como
    // completado y también devolverlo a pendiente.
    @Override
    @Transactional
    public Tratamiento actualizarEstado(Long id, boolean completado) {
        Tratamiento tratamiento = obtenerTratamientoPorId(id);
        tratamiento.setCompletado(completado);
        return tratamientoRepository.save(tratamiento);
    }


    // Elimina un tratamiento validando que exista.
    @Override
    @Transactional
    public void eliminarTratamiento(Long id) {
        obtenerTratamientoPorId(id);
        tratamientoRepository.deleteById(id);
    }
}