package com.vetopia.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"tratamientos"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Droga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String nombre;
    @Column(nullable = false)
    private Double precioCompra = 0.0;
    @Column(nullable = false)
    private Double precioVenta = 0.0;
    @Column(nullable = false)
    private Integer unidadesDisponibles = 0;
    @Column(nullable = false)
    private Integer unidadesVendidas = 0;

    @OneToMany(mappedBy = "droga")
    private List<Tratamiento> tratamientos = new ArrayList<>();

    public Droga(String nombre, Double precioCompra, Double precioVenta, Integer unidadesDisponibles, Integer unidadesVendidas) {
        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.unidadesDisponibles = unidadesDisponibles;
        this.unidadesVendidas = unidadesVendidas;
    }
}