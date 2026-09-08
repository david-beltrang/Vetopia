package com.vetopia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.Droga;


public interface DrogaRepository extends JpaRepository<Droga, Long> {
}