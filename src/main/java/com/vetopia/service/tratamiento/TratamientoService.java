package com.vetopia.service.tratamiento;

import java.util.List;

import com.vetopia.entities.Tratamiento;

// Esta interfaz define los métodos que el servicio de tratamientos debe implementar.
public interface TratamientoService {

    // Devuelve todos los tratamientos registrados
    List<Tratamiento> listarTratamientos();

    // Obtiene un tratamiento validando previamente el identificador
    Tratamiento obtenerTratamientoPorId(Long id);

    // Guarda un tratamiento nuevo. El id lo asigna la base de datos
    void guardar(Tratamiento tratamiento);

    // Devuelve los tratamientos asociados a un veterinario
    List<Tratamiento> listarTratamientosPorVeterinario(Long veterinarioId);

    // Devuelve el historial médico (tratamientos) de una mascota
    List<Tratamiento> listarTratamientosPorMascota(Long mascotaId);

    // Devuelve los tratamientos asociados a un veterinario junto con el
    // nombre de la droga asociada.
    List<HistorialTratamientoMascota> listarTratamientosPorVeterinarioConDroga(Long veterinarioId);

    // Devuelve el historial médico de una mascota con el nombre de la droga.
    List<HistorialTratamientoMascota> listarHistorialDeMascota(Long mascotaId);

    // Valida y aplica un tratamiento: verifica que la mascota y droga
    // existan, que haya stock disponible, guarda el tratamiento y descuenta
    // una unidad del inventario. Devuelve un DTO de confirmación.
    ResultadoTratamiento asignarTratamiento(Tratamiento tratamiento, Long veterinarioId);
}