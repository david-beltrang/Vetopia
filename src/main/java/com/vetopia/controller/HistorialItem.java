package com.vetopia.controller;
import com.vetopia.entities.Tratamiento;


    // Objeto auxiliar que junta un tratamiento con el nombre de su droga,
    // para que la vista de la ficha no tenga que navegar por ids.
    public class HistorialItem {
        private Tratamiento tratamiento;
        private String drogaNombre;

        public HistorialItem(Tratamiento tratamiento, String drogaNombre) {
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
