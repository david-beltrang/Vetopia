package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Tratamiento;
import com.vetopia.repository.TratamientoRepository;

// Esta clase implementa la interfaz TratamientoService y define la lógica de negocio relacionada con tratamientos. Es el único punto al que los controladores acceden para obtener datos; a su vez, delega el acceso a datos en la capa repository.
@Service
public class TratamientoServiceImpl implements TratamientoService {

    //Inyección de dependencias
    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Override
    public List<Tratamiento> listarTratamientos() {
        return List.copyOf(tratamientoRepository.searchAll());
    }

    @Override
    public Tratamiento obtenerTratamientoPorId(Integer id) {
        return tratamientoRepository.searchById(id);
    }

    @Override
    public void guardar(Tratamiento tratamiento) {
        tratamientoRepository.save(tratamiento);
    }

    @Override
    public List<Tratamiento> listarTratamientosPorVeterinario(Integer veterinarioId) {
        return List.copyOf(tratamientoRepository.searchByVeterinarioId(veterinarioId));
    }

    @Override
    public List<Tratamiento> listarTratamientosPorMascota(Integer mascotaId) {
        return List.copyOf(tratamientoRepository.searchByMascotaId(mascotaId));
    }
}
