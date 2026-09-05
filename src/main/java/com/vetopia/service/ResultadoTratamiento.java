package com.vetopia.service;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;

/**
 * DTO de confirmación de un tratamiento recién aplicado. Reúne el
 * tratamiento, la mascota, la droga y el estado del inventario tras
 * descontar las unidades, para que la vista solo tenga que mostrarlos.
 */
public class ResultadoTratamiento {

    private Tratamiento tratamiento;
    private Mascota mascota;
    private Droga droga;
    private int unidadesDescontadas;
    private Integer stockRestante;

    public ResultadoTratamiento(Tratamiento tratamiento, Mascota mascota, Droga droga,
            int unidadesDescontadas) {
        this.tratamiento = tratamiento;
        this.mascota = mascota;
        this.droga = droga;
        this.unidadesDescontadas = unidadesDescontadas;
        this.stockRestante = droga == null ? 0 : droga.getUnidadesDisponibles();
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public Droga getDroga() {
        return droga;
    }

    public int getUnidadesDescontadas() {
        return unidadesDescontadas;
    }

    public Integer getStockRestante() {
        return stockRestante;
    }

    public String getMedicamentoNombre() {
        return droga == null ? null : droga.getNombre();
    }
}