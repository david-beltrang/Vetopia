package com.vetopia.service.tratamiento;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.mascota.Mascota;

/**
 * Confirmación de un tratamiento recién aplicado. Reúne el
 * tratamiento, la mascota, la droga y el estado del inventario tras
 * descontar las unidades, para que la vista solo tenga que mostrarlos.
 */
public class ResultadoTratamiento {

    private Tratamiento tratamiento;
    private Mascota mascota;
    private Droga droga;
    private int unidadesDescontadas;
    private Integer stockRestante;

    // Constructor para inicializar los campos de la clase ResultadoTratamiento
    public ResultadoTratamiento(Tratamiento tratamiento, Mascota mascota, Droga droga,
            int unidadesDescontadas) {
        this.tratamiento = tratamiento;
        this.mascota = mascota;
        this.droga = droga;
        this.unidadesDescontadas = unidadesDescontadas;
        this.stockRestante = droga == null ? 0 : droga.getUnidadesDisponibles();
    }

    // Devuelve el tratamiento asociado al resultado
    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    // Devuelve la mascota asociada al resultado
    public Mascota getMascota() {
        return mascota;
    }

    // Devuelve la droga asociada al resultado
    public Droga getDroga() {
        return droga;
    }

    // Devuelve la cantidad de unidades descontadas del inventario de la droga
    public int getUnidadesDescontadas() {
        return unidadesDescontadas;
    }

    // Devuelve el stock restante de la droga después de descontar las unidades
    public Integer getStockRestante() {
        return stockRestante;
    }

    // Devuelve el nombre del medicamento asociado a la droga
    public String getMedicamentoNombre() {
        return droga == null ? null : droga.getNombre();
    }
}