package com.vetopia.service.tratamiento;

import java.util.List;

import com.vetopia.dto.HistorialTratamientoDTO;
import com.vetopia.dto.ResultadoTratamientoDTO;
import com.vetopia.entities.Tratamiento;

// Esta interfaz define los métodos que el servicio de tratamientos debe implementar.
public interface TratamientoService {

    // Lista todos los tratamientos registrados en la base de datos
    List<Tratamiento> listarTratamientos();

    // Obtiene un tratamiento por su identificador
    Tratamiento obtenerTratamientoPorId(Long id);

    // Guarda un tratamiento en la base de datos
    void guardar(Tratamiento tratamiento);

    // Lista los tratamientos asociados a un veterinario específico
    List<Tratamiento> listarTratamientosPorVeterinario(Long veterinarioId);

    // Lista los tratamientos asociados a una mascota específica
    List<Tratamiento> listarTratamientosPorMascota(Long mascotaId);

    // Lista los tratamientos asociados a un veterinario específico, incluyendo el nombre de la droga utilizada
    List<HistorialTratamientoDTO> listarTratamientosPorVeterinarioConDroga(Long veterinarioId);

    // Lista los tratamientos asociados a una mascota específica, incluyendo el nombre de la droga utilizada
    List<HistorialTratamientoDTO> listarHistorialDeMascota(Long mascotaId);

    // Asigna un tratamiento a un veterinario y devuelve un objeto ResultadoTratamientoDTO que contiene información sobre la operación
    ResultadoTratamientoDTO asignarTratamiento(Tratamiento tratamiento, Long veterinarioId);

    // Elimina un tratamiento por su id
    void eliminarTratamiento(Long id);

    void marcarTratamientoComoCompletado(Long id);

    // Cambia el estado (completado/pendiente) de un tratamiento y lo devuelve
    Tratamiento actualizarEstado(Long id, boolean completado);

    // Retorna el total de tratamientos aplicados registrados en la base de datos
    long contarTratamientosAplicados();
}