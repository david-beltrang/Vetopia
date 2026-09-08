package com.vetopia.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;
import com.vetopia.repository.TratamientoRepository;

@Service
public class TratamientoServiceImpl implements TratamientoService {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private DrogaService drogaService;

    @Override
    public List<Tratamiento> listarTratamientos() {
        return tratamientoRepository.findAll();
    }

    @Override
    public Tratamiento obtenerTratamientoPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del tratamiento.");
        }
        Tratamiento tratamiento = tratamientoRepository.findById(id).orElse(null);
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
        return tratamientoRepository.findByVeterinarioId(veterinarioId);
    }

    @Override
    public List<Tratamiento> listarTratamientosPorMascota(Integer mascotaId) {
        if (mascotaId == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la mascota.");
        }
        return tratamientoRepository.findByMascotaId(mascotaId);
    }

    private String resolverNombreDroga(Droga droga) {
        if (droga != null && droga.getNombre() != null) {
            return droga.getNombre();
        }
        return "Medicamento";
    }

    @Override
    public List<HistorialTratamientoMascota> listarTratamientosPorVeterinarioConDroga(Integer veterinarioId) {
        List<HistorialTratamientoMascota> items = new ArrayList<>();
        for (Tratamiento tratamiento : listarTratamientosPorVeterinario(veterinarioId)) {
            items.add(new HistorialTratamientoMascota(tratamiento, resolverNombreDroga(tratamiento.getDroga())));
        }
        return items;
    }

    @Override
    public List<HistorialTratamientoMascota> listarHistorialDeMascota(Integer mascotaId) {
        List<HistorialTratamientoMascota> historial = new ArrayList<>();
        for (Tratamiento tratamiento : listarTratamientosPorMascota(mascotaId)) {
            historial.add(new HistorialTratamientoMascota(tratamiento, resolverNombreDroga(tratamiento.getDroga())));
        }
        return historial;
    }

    @Override
    @Transactional
    public ResultadoTratamiento asignarTratamiento(Tratamiento tratamiento, Integer veterinarioId) {
        if (tratamiento == null) {
            throw new IllegalArgumentException("No se especificó el tratamiento a asignar.");
        }
        if (tratamiento.getMascota() == null || tratamiento.getDroga() == null) {
            throw new IllegalArgumentException("Debes seleccionar un paciente y un medicamento para asignar el tratamiento.");
        }

        Droga droga = tratamiento.getDroga();

        int unidadesDisponibles = droga.getUnidadesDisponibles() == null ? 0 : droga.getUnidadesDisponibles();
        if (unidadesDisponibles <= 0) {
            throw new IllegalArgumentException(
                    "El medicamento \"" + droga.getNombre() + "\" no tiene unidades disponibles.");
        }

        if (tratamiento.getVeterinario() == null) {
            throw new IllegalArgumentException("No se identificó al veterinario que aplica el tratamiento.");
        }

        guardar(tratamiento);
        drogaService.descontarUnidades(droga.getId(), 1);

        Mascota mascota = tratamiento.getMascota();
        return new ResultadoTratamiento(tratamiento, mascota, droga, 1);
    }

    @Override
    public void eliminarTratamiento(Integer id) {
        Tratamiento tratamiento = obtenerTratamientoPorId(id);
        tratamientoRepository.delete(tratamiento);
    }
}