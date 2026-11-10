package com.vetopia.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.vetopia.entities.EstadoCita;

public record CitaDTO(
        Long id,
        Long duenoId,
        String duenoNombre,
        Long veterinarioId,
        String veterinarioNombre,
        String veterinarioEspecialidad,
        List<MascotaCitaDTO> mascotas,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        String motivo,
        String especialidad,
        EstadoCita estado) {
}
