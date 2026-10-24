package com.vetopia.dto;

import com.vetopia.entities.Tratamiento;

/**
 * DTO auxiliar que junta un tratamiento con el nombre de su droga para el
 * listado de tratamientos de un veterinario. Se deja como clase propia (igual
 * que HistorialTratamientoDTO) por si el listado necesita separarse del
 * historial de una mascota más adelante.
 */
public class TratamientoListItemDTO {

    private Tratamiento tratamiento;
    private String drogaNombre;

    public TratamientoListItemDTO(Tratamiento tratamiento, String drogaNombre) {
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
