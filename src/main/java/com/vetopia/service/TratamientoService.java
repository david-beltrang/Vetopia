package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Tratamiento;

// Esta interfaz define los métodos que el servicio de tratamientos debe implementar.
public interface TratamientoService {

    // Devuelve todos los tratamientos registrados
    List<Tratamiento> listarTratamientos();

    // Obtiene un tratamiento validando previamente el identificador
    Tratamiento obtenerTratamientoPorId(Integer id);

    // Guarda un tratamiento nuevo. El id lo asigna el repositorio
    void guardar(Tratamiento tratamiento);

    // Devuelve los tratamientos asociados a un veterinario
    List<Tratamiento> listarTratamientosPorVeterinario(Integer veterinarioId);

    // Devuelve el historial médico (tratamientos) de una mascota
    List<Tratamiento> listarTratamientosPorMascota(Integer mascotaId);

    // Devuelve los tratamientos asociados a un veterinario junto con el
    // nombre de la droga asociada.
    List<HistorialTratamientoMascota> listarTratamientosPorVeterinarioConDroga(Integer veterinarioId);

    // Devuelve el historial médico de una mascota con el nombre de la droga.
    List<HistorialTratamientoMascota> listarHistorialDeMascota(Integer mascotaId);

    // Valida y aplica un tratamiento: verifica que la mascota y droga
    // existan, que haya stock disponible, guarda el tratamiento y descuenta
    // una unidad del inventario. Devuelve un DTO de confirmación.
    ResultadoTratamiento asignarTratamiento(Tratamiento tratamiento, Integer veterinarioId);

    // Elimina los tratamientos de una mascota (borrado en cascada).
    void eliminarTratamientosDeMascota(Integer mascotaId);
}