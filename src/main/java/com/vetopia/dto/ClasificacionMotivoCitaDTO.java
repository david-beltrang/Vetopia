package com.vetopia.dto;

import java.util.List;

public record ClasificacionMotivoCitaDTO(
        String especialidad,
        boolean requiereSeleccionManual,
        List<String> especialidadesDisponibles,
        String mensaje) {
}
