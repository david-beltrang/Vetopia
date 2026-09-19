package com.vetopia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Veterinario;

// Repositorio JPA de Veterinario.
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    // Consulta derivada para el login del portal. Devuelve null si no hay coincidencia.
    Veterinario findByCorreoIgnoreCaseAndContrasena(String correo, String contrasena);
}