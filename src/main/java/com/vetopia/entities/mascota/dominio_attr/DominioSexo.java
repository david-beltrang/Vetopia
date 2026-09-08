package com.vetopia.entities.mascota.dominio_attr;

public enum DominioSexo {
    MACHO("Macho"),
    HEMBRA("Hembra");

    private final String valor;

    DominioSexo(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}