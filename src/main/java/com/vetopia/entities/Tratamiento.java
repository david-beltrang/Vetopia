package com.vetopia.entities;

import java.time.LocalDate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.vetopia.entities.mascota.Mascota;

// Tratamiento. Guarda la fecha, el título,
// la descripción y si ya ha sido completado, tal como se muestra en el
// historial de la ficha clínica. Sus tres relaciones n-1 son la parte
// dueña de la FK
@Entity
@Table(name = "tratamientos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    // Relación n-1 con Mascota: esta es la parte dueña de la FK.
    // Queda visible en el JSON para que el frontend muestre el paciente del
    // tratamiento; no hay recursión porque Mascota.tratamientos está ignorada.
    @ToString.Exclude
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    // Relación n-1 con Veterinario: esta es la parte dueña de la FK.
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "veterinario_id", nullable = true)
    private Veterinario veterinario;

    // Relación n-1 con Droga: esta es la parte dueña de la FK.
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "droga_id", nullable = false)
    private Droga droga;

    @Column(length = 150, nullable = false)
    private String titulo;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Boolean completado = false;

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
