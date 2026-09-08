package com.vetopia.entities;

import java.util.ArrayList;
import java.util.List;

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


// Veterinario (administrador de la veterinaria). Es quien registra los
// tratamientos de las mascotas en el inventario de atenciones del sistema.
@Getter
@Setter
@ToString(exclude = {"tratamientos"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Veterinario {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String cedula;
    private String contrasena;
    private String correo;
    private String especialidad;
    private Integer numeroAtenciones;
    private String nombre;
    private String estado;
    private String urlFoto;

    @OneToMany(mappedBy = "veterinario")
    private List<Tratamiento> tratamientos = new ArrayList<>();

    public Veterinario(String cedula, String contrasena, String correo, String especialidad, Integer numeroAtenciones, String nombre, String estado, String urlFoto) {
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