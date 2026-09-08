package com.vetopia.entities;

import java.util.ArrayList;
import java.util.List;

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
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.vetopia.entities.mascota.dominio_attr.DominioEstado;

// Entidad Veterinario. Relación 1-n con Tratamiento: mappedBy apunta al
// campo "veterinario" de Tratamiento.
@Entity
@Table(name = "veterinarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String cedula;

    @Column(length = 100, nullable = false)
    private String contrasena;

    @Column(length = 100, nullable = false)
    private String correo;

    @Column(length = 100, nullable = false)
    private String especialidad;

    @Column(nullable = false)
    private Integer numeroAtenciones;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private DominioEstado estado;

    @Column(name = "url_foto", length = 500)
    private String urlFoto;

    @ToString.Exclude
    @OneToMany(mappedBy = "veterinario")
    private List<Tratamiento> tratamientos = new ArrayList<>();

        public Veterinario(String cedula, String contrasena, String correo, String especialidad, Integer numeroAtenciones, String nombre, DominioEstado estado, String urlFoto) {
        this.cedula = cedula;
        this.contrasena = contrasena;
        this.correo = correo;
        this.especialidad = especialidad;
        this.numeroAtenciones = numeroAtenciones;
        this.nombre = nombre;
        this.estado = estado;
        this.urlFoto = urlFoto;
    }
}