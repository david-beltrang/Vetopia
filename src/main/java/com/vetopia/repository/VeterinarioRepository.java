package com.vetopia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.vetopia.entities.Veterinario;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;

// Repositorio JPA de Veterinario
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    // Consulta derivada para el login del portal. Devuelve null si no hay coincidencia
    Veterinario findByCorreoIgnoreCaseAndContrasena(String correo, String contrasena);

    Veterinario findByCorreoIgnoreCase(String correo);

    // Cuenta veterinarios activos o inactivos para los indicadores
    long countByEstado(DominioEstado estado);

    List<Veterinario> findByEstadoAndEspecialidadIgnoreCase(DominioEstado estado, String especialidad);

    @Query("select distinct v.especialidad from Veterinario v where v.estado = :estado order by v.especialidad")
    List<String> listarEspecialidadesPorEstado(@Param("estado") DominioEstado estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Veterinario v where v.id = :id")
    java.util.Optional<Veterinario> buscarPorIdConBloqueo(@Param("id") Long id);
}
