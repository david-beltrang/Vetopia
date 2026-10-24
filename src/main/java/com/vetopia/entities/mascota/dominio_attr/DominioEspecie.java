package com.vetopia.entities.mascota.dominio_attr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.vetopia.exception.ParametroInvalidoException;

// Enum que representa las especies de mascotas disponibles en Vetopia
public enum DominioEspecie {
    PERRO("Perro"),
    GATO("Gato"),
    OTRO("Otro");

    private final String valor;

    DominioEspecie(String valor) {
        this.valor = valor;
    }

    // El JSON debe mostrar "Perro" y no "PERRO"; @JsonValue fuerza a Jackson a
    // serializar con este getter en vez del nombre del enum.
    @JsonValue
    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor;
    }

    // @JsonCreator hace que al deserializar se acepte "Perro"/"Gato"/"Otro"
    // en lugar del nombre en mayúsculas del enum.
    @JsonCreator
    public static DominioEspecie fromValor(String valor) {
        for (DominioEspecie e : values()) {
            if (e.valor.equalsIgnoreCase(valor)) {
                return e;
            }
        }
        throw new ParametroInvalidoException("Especie desconocida: " + valor);
    }
}