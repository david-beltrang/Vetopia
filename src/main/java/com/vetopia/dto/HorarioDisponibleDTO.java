package com.vetopia.dto;

import java.time.LocalDateTime;

public record HorarioDisponibleDTO(
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin) {
}
