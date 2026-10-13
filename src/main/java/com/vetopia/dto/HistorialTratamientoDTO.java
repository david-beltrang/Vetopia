package com.vetopia.dto;

import com.vetopia.entities.Tratamiento;

/**
 * DTO de salida para el historial clínico: junta un tratamiento con el nombre
 * de su droga. El profesor no usó DTOs en el video, pero aquí sirven para no
 * obligar a Angular a recorrer ids ni a exponer toda la entidad Droga solo
 * para mostrar su nombre en la ficha de la mascota.
 */
public class HistorialTratamientoDTO {

    private Tratamiento tratamiento;
    private String drogaNombre;

    public HistorialTratamientoDTO(Tratamiento tratamiento, String drogaNombre) {
        this.tratamiento = tratamiento;
        this.drogaNombre = drogaNombre;
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public String getDrogaNombre() {
        return drogaNombre;
    }
}
