package com.vetopia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Administrador;

// Repositorio JPA de Administrador.
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    // Consulta derivada para el login del portal.
    Optional<Administrador> findByCorreoIgnoreCaseAndContrasena(String correo, String contrasena);
}