package com.vetopia.entities.mascota.dominio_attr;

import com.vetopia.exception.ParametroInvalidoException;

// Enum que representa los estados posibles de una mascota en Vetopia
public enum DominioEstado {
    ACTIVO("Activo"),
    INACTIVO("Inactivo");

    private final String valor;

    DominioEstado(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor;
    }

    public static DominioEstado fromValor(String valor) {
        for (DominioEstado e : values()) {
            if (e.valor.equalsIgnoreCase(valor)) {
                return e;
            }
        }
        throw new ParametroInvalidoException("Estado desconocido: " + valor);
    }
}
