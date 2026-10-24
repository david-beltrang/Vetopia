package com.vetopia.entities.mascota.dominio_attr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.vetopia.exception.ParametroInvalidoException;

// Enum que representa los sexos posibles de una mascota en Vetopia
public enum DominioSexo {
    MACHO("Macho"),
    HEMBRA("Hembra");

    private final String valor;

    DominioSexo(String valor) {
        this.valor = valor;
    }

    // Igual que en los otros enums, @JsonValue hace que el JSON lleve "Macho"
    // y no "MACHO".
    @JsonValue
    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor;
    }

    // Este enum no tenía fromValor; se agrega sobre todo para que Jackson
    // pueda deserializar "Macho"/"Hembra" cuando llega una mascota en el body.
    @JsonCreator
    public static DominioSexo fromValor(String valor) {
        for (DominioSexo e : values()) {
            if (e.valor.equalsIgnoreCase(valor)) {
                return e;
            }
        }
        throw new ParametroInvalidoException("Sexo desconocido: " + valor);
    }
}