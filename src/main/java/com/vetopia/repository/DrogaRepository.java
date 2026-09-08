package com.vetopia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Droga;

// Repositorio JPA de drogas. Spring Data genera la implementación.
public interface DrogaRepository extends JpaRepository<Droga, Long> {
}