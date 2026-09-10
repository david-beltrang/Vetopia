package com.vetopia.service.tratamiento;

import com.vetopia.entities.Tratamiento;

// Clase auxiliar que junta un tratamiento con el nombre de su droga,
// para que las vistas no tengan que navegar por ids.
public class TratamientoListItem {
    private Tratamiento tratamiento;
    private String drogaNombre;

    public TratamientoListItem(Tratamiento tratamiento, String drogaNombre) {
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
