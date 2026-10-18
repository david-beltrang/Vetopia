package com.vetopia.dto;

import com.vetopia.entities.Tratamiento;

/**
 * DTO de salida para el historial clínico: junta un tratamiento con el nombre
 * de su droga y el nombre del cliente dueño de la mascota. El profesor no usó
 * DTOs en el video, pero aquí sirven para no obligar a Angular a recorrer ids
 * ni a exponer las entidades Droga/Dueno completas solo para mostrar su nombre
 * en la ficha de la mascota.
 */
public class HistorialTratamientoDTO {

    private Tratamiento tratamiento;
    private String drogaNombre;
    private String clienteNombre;

    public HistorialTratamientoDTO(Tratamiento tratamiento, String drogaNombre, String clienteNombre) {
        this.tratamiento = tratamiento;
        this.drogaNombre = drogaNombre;
        this.clienteNombre = clienteNombre;
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public String getDrogaNombre() {
        return drogaNombre;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }
}
