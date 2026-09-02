package com.vetopia.entities;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// Tratamiento (registro médico de una mascota). Además de las claves foráneas
// (mascota, veterinario y droga), guarda el título, la descripción y si ya
// ha sido completado, tal como se muestra en el historial de la ficha clínica.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tratamiento {
    private Integer id;
    private LocalDate fecha;
    private Integer mascotaId;
    private Integer veterinarioId;
    private Integer drogaId;
    private String titulo;
    private String descripcion;
    private Boolean completado;
}
