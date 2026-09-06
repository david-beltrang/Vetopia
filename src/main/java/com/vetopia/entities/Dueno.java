package com.vetopia.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.vetopia.entities.mascota.Mascota;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;

/**
 * ENTIDAD - Dueno
 * Representa el dueño (cliente) de una o varias mascotas registradas en
 * Vetopia. La relación con Mascota es 1---n: el lado 1 declara mappedBy
 * apuntando al campo "dueno" que vive en Mascota (dueño de la FK).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "duenos")
public class Dueno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String cedula;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 100, nullable = false)
    private String correo;

    @Column(length = 100, nullable = false)
    private String contrasena;

    @Column(length = 20)
    private String celular;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private DominioEstado estado;

    // Relación 1---n: cada dueño posee varias mascotas. El mappedBy apunta al
    // campo "dueno" de Mascota (donde se guarda la FK dueno_id). Con cascade +
    // orphanRemoval, borrar el dueño borra sus mascotas en cascada. Se excluye
    // de toString/equals para evitar recursión infinita con Mascota.
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "dueno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mascota> mascotas = new ArrayList<>();
}