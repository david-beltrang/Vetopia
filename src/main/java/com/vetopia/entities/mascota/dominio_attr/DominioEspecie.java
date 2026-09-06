package com.vetopia.entities.mascota.dominio_attr;

public enum DominioEspecie {
    PERRO("Perro"),
    GATO("Gato"),
    OTRO("Otro");

    private final String valor;

    DominioEspecie(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor;
    }

    public static DominioEspecie fromValor(String valor) {
        for (DominioEspecie e : values()) {
            if (e.valor.equalsIgnoreCase(valor)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Especie desconocida: " + valor);
    }
}