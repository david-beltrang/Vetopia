package com.vetopia.service;

import com.vetopia.entities.Tratamiento;

/**
 * DTO que junta un tratamiento con el nombre de su droga asociada,
 * para que las vistas no tengan que navegar por identificadores.
 * Se usa tanto en el historial de la ficha clínica como en el
 * listado de tratamientos asignados a un veterinario.
 */
public class HistorialTratamientoMascota {

    private Tratamiento tratamiento;
    private String drogaNombre;

    public HistorialTratamientoMascota(Tratamiento tratamiento, String drogaNombre) {
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