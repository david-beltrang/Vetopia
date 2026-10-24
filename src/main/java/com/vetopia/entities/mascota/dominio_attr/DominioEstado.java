package com.vetopia.entities.mascota.dominio_attr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.vetopia.exception.ParametroInvalidoException;

// Enum que representa los estados posibles de una mascota en Vetopia
public enum DominioEstado {
    ACTIVO("Activo"),
    INACTIVO("Inactivo");

    private final String valor;

    DominioEstado(String valor) {
        this.valor = valor;
    }

    // Jackson por defecto escribiría el nombre del enum ("ACTIVO"), pero el
    // frontend trabaja con el texto que ve el usuario ("Activo"). @JsonValue
    // le dice que serialice con este getter en lugar del name().
    @JsonValue
    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor;
    }

    // Al recibir un JSON, Jackson usaría el name() por defecto; @JsonCreator
    // redirige la conversión a este método para aceptar "Activo"/"Inactivo"
    // igual que los escribe el resto del sistema.
    @JsonCreator
    public static DominioEstado fromValor(String valor) {
        for (DominioEstado e : values()) {
            if (e.valor.equalsIgnoreCase(valor)) {
                return e;
            }
        }
        throw new ParametroInvalidoException("Estado desconocido: " + valor);
    }
}
