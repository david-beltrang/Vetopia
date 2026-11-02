package com.vetopia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetopia.entities.mascota.Mascota;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    // Devuelve las mascotas (pacientes) de un dueño especifico
    // El guion bajo obliga a navegar la relación (dueno -> id) y evita
    // que Spring Data lo confunda con el getter duenoId() de la entidad.
    List<Mascota> findByDueno_Id(Long duenoId);

    // Busca mascotas por nombre
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);

    // Cuenta las mascotas segun su estado actual
    long countByEstado(DominioEstado estado);
}
