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

/**
 * ENTIDAD - Dueno
 * Representa el dueño (cliente) de una o varias mascotas registradas en
 * Vetopia. No contiene lógica de acceso a datos ni de presentación, solo
 * el estado y las reglas propias de la entidad.
 */
@Getter
@Setter
@ToString(exclude = {"mascotas"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Dueno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 10, unique = true, nullable = false)
    private String cedula;
    @Column(length = 100, nullable = false)
    private String nombre;
    @Column(length = 150, unique = true, nullable = false)
    private String correo;
    @Column(length = 100, nullable = false)
    private String contrasena;
    @Column(length = 10, unique = true, nullable = false)
    private String celular;
    @Column(length = 20, nullable = false)
    private String estado = "Activo";

    @OneToMany(mappedBy = "dueno")
    private List<Mascota> mascotas = new ArrayList<>();

    public Dueno(String cedula, String nombre, String correo, String contrasena, String celular, String estado) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.celular = celular;
        this.estado = estado;
    }
}