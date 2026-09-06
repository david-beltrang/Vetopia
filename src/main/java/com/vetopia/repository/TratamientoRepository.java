package com.vetopia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Tratamiento;

// Repositorio JPA de Tratamiento. Las consultas derivadas atraviesan las
// relaciones mascota y veterinario.
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    // Devuelve los tratamientos (historial médico) de una mascota.
    List<Tratamiento> findByMascotaId(Long mascotaId);

    // Devuelve los tratamientos asociados a un veterinario.
    List<Tratamiento> findByVeterinarioId(Long veterinarioId);
}