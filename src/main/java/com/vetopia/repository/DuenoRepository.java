package com.vetopia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Dueno;

// Repositorio JPA de Dueno. Hereda métodos predefinidos (findAll, findById,
// save, deleteById...) y se agregan consultas derivadas por el nombre del método.
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    // Consulta derivada para el login: busca por correo (ignorando mayúsculas)
    // y contraseña. Spring Data JPA genera el JPQL a partir del nombre.
    Optional<Dueno> findByCorreoIgnoreCaseAndContrasena(String correo, String contrasena);
}