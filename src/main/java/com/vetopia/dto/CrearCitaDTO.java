package com.vetopia.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CrearCitaDTO(
        Long duenoId,
        List<Long> mascotaIds,
        String motivo,
        String especialidad,
        LocalDateTime fechaHoraInicio) {
}
