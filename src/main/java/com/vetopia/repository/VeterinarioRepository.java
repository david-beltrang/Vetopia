package com.vetopia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Veterinario;

// Repositorio JPA de veterinarios. Spring Data genera la implementación.
public interface VeterinarioRepository extends JpaRepository<Veterinario, Integer> {

    // Busca un veterinario por su correo y contraseña (para login)
    Optional<Veterinario> findByCorreoIgnoreCaseAndContrasena(String correo, String contrasena);
}