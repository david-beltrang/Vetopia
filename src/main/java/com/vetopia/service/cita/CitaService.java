package com.vetopia.service.cita;

import java.time.LocalDate;
import java.util.List;

import com.vetopia.dto.CitaDTO;
import com.vetopia.dto.ClasificacionMotivoCitaDTO;
import com.vetopia.dto.CrearCitaDTO;
import com.vetopia.dto.HorarioDisponibleDTO;
import com.vetopia.entities.EstadoCita;

public interface CitaService {

    List<String> listarEspecialidadesDisponibles();

    ClasificacionMotivoCitaDTO clasificarMotivo(String motivo);

    List<HorarioDisponibleDTO> listarDisponibilidad(LocalDate fecha, int cantidadMascotas, String especialidad);

    CitaDTO crear(CrearCitaDTO solicitud);

    List<CitaDTO> listarDeDueno(Long duenoId);

    List<CitaDTO> listarDeVeterinario(Long veterinarioId, LocalDate desde, LocalDate hasta);

    List<CitaDTO> listarTodas(LocalDate desde, LocalDate hasta);

    CitaDTO cancelarDeDueno(Long citaId, Long duenoId);

    CitaDTO cancelarComoAdministrador(Long citaId);

    CitaDTO actualizarEstadoVeterinario(Long citaId, Long veterinarioId, EstadoCita estado);
}
