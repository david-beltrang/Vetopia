package com.vetopia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Mascota;

// Repositorio JPA de mascotas. Spring Data genera la implementación.
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    // Devuelve las mascotas (pacientes) de un dueño concreto.
    List<Mascota> findByDuenoId(Long duenoId);
}