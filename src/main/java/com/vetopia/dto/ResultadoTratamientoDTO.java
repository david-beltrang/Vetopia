package com.vetopia.dto;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.mascota.Mascota;

/**
 * Confirmación de un tratamiento recién aplicado. Reúne el tratamiento, la
 * mascota, la droga y el estado del inventario tras descontar las unidades,
 * para que el frontend solo tenga que mostrar estos valores.
 */
public class ResultadoTratamientoDTO {

    private Tratamiento tratamiento;
    private Mascota mascota;
    private Droga droga;
    private int unidadesDescontadas;
    private Integer stockRestante;

    public ResultadoTratamientoDTO(Tratamiento tratamiento, Mascota mascota, Droga droga,
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

    // El frontend muestra el nombre del medicamento, no el objeto Droga completo.
    public String getMedicamentoNombre() {
        return droga == null ? null : droga.getNombre();
    }
}
