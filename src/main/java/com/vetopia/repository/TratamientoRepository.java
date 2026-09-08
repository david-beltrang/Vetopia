package com.vetopia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Tratamiento;

// Repositorio JPA de tratamientos. Spring Data genera la implementación.
public interface TratamientoRepository extends JpaRepository<Tratamiento, Integer> {

    // Devuelve los tratamientos asociados a un veterinario.
    List<Tratamiento> findByVeterinarioId(Integer veterinarioId);

    // Devuelve los tratamientos (historial médico) de una mascota.
    List<Tratamiento> findByMascotaId(Integer mascotaId);
}