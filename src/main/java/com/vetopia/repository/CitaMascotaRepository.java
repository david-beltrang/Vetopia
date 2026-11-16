package com.vetopia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.CitaMascota;

public interface CitaMascotaRepository extends JpaRepository<CitaMascota, Long> {

    List<CitaMascota> findByCita_Id(Long citaId);
}
