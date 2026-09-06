package com.vetopia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Veterinario;

// Repositorio JPA de Veterinario.
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    // Consulta derivada para el login del portal.
    Optional<Veterinario> findByCorreoIgnoreCaseAndContrasena(String correo, String contrasena);
}