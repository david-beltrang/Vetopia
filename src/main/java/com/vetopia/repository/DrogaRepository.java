package com.vetopia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Droga;

// Repositorio JPA de Droga. Hereda las operaciones básicas de JpaRepository
// (findById, findAll, save...) que cubren las necesidades del servicio.
public interface DrogaRepository extends JpaRepository<Droga, Long> {
}