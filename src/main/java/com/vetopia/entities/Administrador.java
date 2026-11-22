package com.vetopia.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad Administrador
@Entity
@Table(name = "administradores")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Administrador {

    @OneToOne(cascade = CascadeType.ALL)
    private UserEntity user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String cedula;

    @Column(length = 100, nullable = false)
    private String correo;

    @Column(length = 100, nullable = false)
    private String contrasena;

    @Column(length = 100, nullable = false)
    private String nombre;
}