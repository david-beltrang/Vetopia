package com.vetopia.service.tratamiento;

import java.util.List;

import com.vetopia.entities.Tratamiento;

// Esta interfaz define los métodos que el servicio de tratamientos debe implementar.
public interface TratamientoService {

    
    List<Tratamiento> listarTratamientos();

    
    Tratamiento obtenerTratamientoPorId(Long id);

   
    void guardar(Tratamiento tratamiento);

    
    List<Tratamiento> listarTratamientosPorVeterinario(Long veterinarioId);

    
    List<Tratamiento> listarTratamientosPorMascota(Long mascotaId);

   
    List<HistorialTratamientoMascota> listarTratamientosPorVeterinarioConDroga(Long veterinarioId);

    
    List<HistorialTratamientoMascota> listarHistorialDeMascota(Long mascotaId);

    
    ResultadoTratamiento asignarTratamiento(Tratamiento tratamiento, Long veterinarioId);
}