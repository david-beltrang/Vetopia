package com.vetopia.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.TratamientoRepository;

@Service
public class TratamientoServiceImpl implements TratamientoService {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private DrogaService drogaService;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Override
    public List<Tratamiento> listarTratamientos() {
        Collection<Tratamiento> tratamientos = tratamientoRepository.searchAll();
        return tratamientos == null ? List.of() : List.copyOf(tratamientos);
    }

    @Override
    public Tratamiento obtenerTratamientoPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del tratamiento.");
        }
        Tratamiento tratamiento = tratamientoRepository.searchById(id);
        if (tratamiento == null) {
            throw new IllegalArgumentException("No se encontró ningún tratamiento con el identificador " + id + ".");
        }
        return tratamiento;
    }

    @Override
    public void guardar(Tratamiento tratamiento) {
        tratamientoRepository.save(tratamiento);
    }

    @Override
    public List<Tratamiento> listarTratamientosPorVeterinario(Integer veterinarioId) {
        if (veterinarioId == null) {
            throw new IllegalArgumentException("No se especificó el identificador del veterinario.");
        }
        return List.copyOf(tratamientoRepository.searchByVeterinarioId(veterinarioId));
    }

    @Override
    public List<Tratamiento> listarTratamientosPorMascota(Integer mascotaId) {
        if (mascotaId == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la mascota.");
        }
        return List.copyOf(tratamientoRepository.searchByMascotaId(mascotaId));
    }

    private String resolverNombreDroga(Integer drogaId) {
        String nombreDroga = "Medicamento";
        if (drogaId != null) {
            try {
                Droga droga = drogaService.obtenerDrogaPorId(drogaId);
                if (droga != null && droga.getNombre() != null) {
                    nombreDroga = droga.getNombre();
                }
            } catch (IllegalArgumentException e) {
                nombreDroga = "Medicamento";
            }
        }
        return nombreDroga;
    }

    @Override
    public List<HistorialTratamientoMascota> listarTratamientosPorVeterinarioConDroga(Integer veterinarioId) {
        List<HistorialTratamientoMascota> items = new ArrayList<>();
        for (Tratamiento tratamiento : listarTratamientosPorVeterinario(veterinarioId)) {
            items.add(new HistorialTratamientoMascota(tratamiento, resolverNombreDroga(tratamiento.getDrogaId())));
        }
        return items;
    }

    @Override
    public List<HistorialTratamientoMascota> listarHistorialDeMascota(Integer mascotaId) {
        List<HistorialTratamientoMascota> historial = new ArrayList<>();
        for (Tratamiento tratamiento : listarTratamientosPorMascota(mascotaId)) {
            historial.add(new HistorialTratamientoMascota(tratamiento, resolverNombreDroga(tratamiento.getDrogaId())));
        }
        return historial;
    }

    @Override
    public ResultadoTratamiento asignarTratamiento(Tratamiento tratamiento, Integer veterinarioId) {
        if (tratamiento == null) {
            throw new IllegalArgumentException("No se especificó el tratamiento a asignar.");
        }
        if (tratamiento.getMascotaId() == null || tratamiento.getDrogaId() == null) {
            throw new IllegalArgumentException("Debes seleccionar un paciente y un medicamento para asignar el tratamiento.");
        }

        Droga droga = drogaService.obtenerDrogaPorId(tratamiento.getDrogaId());

        int unidadesDisponibles = droga.getUnidadesDisponibles() == null ? 0 : droga.getUnidadesDisponibles();
        if (unidadesDisponibles <= 0) {
            throw new IllegalArgumentException(
                    "El medicamento \"" + droga.getNombre() + "\" no tiene unidades disponibles.");
        }

        Integer idVeterinario = veterinarioId != null ? veterinarioId : tratamiento.getVeterinarioId();
        if (idVeterinario == null) {
            throw new IllegalArgumentException("No se identificó al veterinario que aplica el tratamiento.");
        }
        tratamiento.setVeterinarioId(idVeterinario);

        guardar(tratamiento);
        drogaService.descontarUnidades(tratamiento.getDrogaId(), 1);

        Mascota mascota = mascotaRepository.searchById(tratamiento.getMascotaId());
        if (mascota == null) {
            throw new IllegalArgumentException("El paciente seleccionado no existe.");
        }
        return new ResultadoTratamiento(tratamiento, mascota, droga, 1);
    }
}