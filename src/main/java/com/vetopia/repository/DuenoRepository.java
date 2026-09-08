package com.vetopia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Dueno;

// Repositorio JPA de dueños. Spring Data genera la implementación.
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    // Busca un dueño por su correo electrónico y contraseña, devolviendo el
    // objeto Dueno correspondiente si se encuentra una coincidencia.
    Optional<Dueno> findByCorreoIgnoreCaseAndContrasena(String correo, String contrasena);
}