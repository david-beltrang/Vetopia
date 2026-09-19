package com.vetopia.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
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


import com.vetopia.entities.mascota.Mascota;

/**
 * ENTIDAD - Dueno
 * Representa el dueño (cliente) de una o varias mascotas registradas en
 * Vetopia. La relación con Mascota es 1---n: el lado 1 declara mappedBy
 * apuntando al campo "dueno" que vive en Mascota (dueño de la FK).
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

    @OneToMany(mappedBy = "dueno", cascade = CascadeType.ALL)
    private List<Mascota> mascotas = new ArrayList<>();
}