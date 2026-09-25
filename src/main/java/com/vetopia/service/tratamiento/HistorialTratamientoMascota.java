package com.vetopia.service.tratamiento;

import com.vetopia.entities.Tratamiento;

/*
 * Junta un tratamiento con el nombre de su droga asociada,
 * para que las vistas no tengan que navegar por identificadores
 */
public class HistorialTratamientoMascota {

    private Tratamiento tratamiento;
    private String drogaNombre;

    // Constructor para inicializar los campos de la clase HistorialTratamientoMascota
    public HistorialTratamientoMascota(Tratamiento tratamiento, String drogaNombre) {
        this.tratamiento = tratamiento;
        this.drogaNombre = drogaNombre;
    }
    
    // Devuelve el tratamiento asociado al historial
    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    // Devuelve el nombre de la droga asociada al tratamiento
    public String getDrogaNombre() {
        return drogaNombre;
    }
}