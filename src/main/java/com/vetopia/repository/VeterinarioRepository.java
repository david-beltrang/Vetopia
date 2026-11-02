package com.vetopia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Veterinario;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;

// Repositorio JPA de Veterinario
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    // Consulta derivada para el login del portal. Devuelve null si no hay coincidencia
    Veterinario findByCorreoIgnoreCaseAndContrasena(String correo, String contrasena);

    Veterinario findByCorreoIgnoreCase(String correo);

    // Cuenta veterinarios activos o inactivos para los indicadores
    long countByEstado(DominioEstado estado);
}
