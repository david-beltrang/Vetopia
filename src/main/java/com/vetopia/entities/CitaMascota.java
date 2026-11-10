package com.vetopia.entities;

import com.vetopia.entities.mascota.Mascota;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Asociación explícita para registrar las mascotas que forman una cita grupal. */
@Entity
@Table(name = "cita_mascotas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitaMascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cita_id", nullable = false)
    @ToString.Exclude
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    @ToString.Exclude
    private Mascota mascota;
}
