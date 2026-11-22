package com.vetopia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Dueno;


public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    // Consulta derivada para el login del portal. Devuelve null si no hay coincidencia.
    Dueno findByCorreoIgnoreCaseAndContrasena(String correo, String contrasena);

    // Consulta derivada para buscar un dueño por su cédula.
    java.util.Optional<Dueno> findByCedula(String cedula);

    // Consulta derivada para buscar un dueño por su correo (usado en /details).
    java.util.Optional<Dueno> findByCorreo(String correo);
}