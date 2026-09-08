package com.vetopia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Administrador;

// Repositorio JPA de administradores. Spring Data genera la implementación.
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    // Busca un administrador por su correo y contraseña (para login)
    Optional<Administrador> findByCorreoIgnoreCaseAndContrasena(String correo, String contrasena);
}