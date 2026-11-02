package com.vetopia.dto;

public record DashboardKpisDTO(
        long totalMascotas,
        long mascotasEnTratamiento,
        long tratamientosUltimoMes,
        double ventasTotales,
        double gananciasTotales) {
}
