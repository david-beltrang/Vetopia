package com.vetopia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.mascota.Mascota;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    // Devuelve las mascotas (pacientes) de un dueño especifico
    List<Mascota> findByDuenoId(Long duenoId);

    // Busca mascotas por nombre
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);
}