package com.vetopia.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// Entidad Droga. Relación 1-n con Tratamiento: mappedBy apunta
// al campo "droga" de Tratamiento.
@Entity
@Table(name = "drogas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Droga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double precioCompra;

    @Column(nullable = false)
    private Double precioVenta;

    @Column(nullable = false)
    private Integer unidadesDisponibles;

    @Column(nullable = false)
    private Integer unidadesVendidas;

    @ToString.Exclude
    @OneToMany(mappedBy = "droga")
    private List<Tratamiento> tratamientos = new ArrayList<>();
}