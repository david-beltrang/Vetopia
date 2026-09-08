package com.vetopia.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


// Tratamiento (registro médico de una mascota). Además de las claves foráneas
// (mascota, veterinario y droga), guarda el título, la descripción y si ya
// ha sido completado, tal como se muestra en el historial de la ficha clínica.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Tratamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate fecha;
    private String titulo;
    private String descripcion;
    private Boolean completado;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Mascota mascota;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Veterinario veterinario;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Droga droga;

    public Tratamiento(LocalDate fecha, Mascota mascota, Veterinario veterinario, Droga droga, String titulo, String descripcion, Boolean completado) {
        this.fecha = fecha;
        this.mascota = mascota;
        this.veterinario = veterinario;
        this.droga = droga;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.completado = completado;
    }
}