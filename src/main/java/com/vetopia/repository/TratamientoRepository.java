package com.vetopia.repository;

import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import com.vetopia.entities.Tratamiento;


public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    // Devuelve los tratamientos (historial médico) de una mascota
    List<Tratamiento> findByMascotaId(Long mascotaId);

    // Devuelve los tratamientos asociados a un veterinario
    List<Tratamiento> findByVeterinarioId(Long veterinarioId);

    long countByVeterinarioId(Long veterinarioId);

    
    // COALESCE Evita devolver null cuando aún no existen tratamientos registrados
    @Query("SELECT COALESCE(SUM(t.droga.precioVenta), 0.0) FROM Tratamiento t")
    double sumVentasAplicadas();

    @Query("SELECT COALESCE(SUM(t.droga.precioVenta - t.droga.precioCompra), 0.0) FROM Tratamiento t")
    double sumGananciasAplicadas();

    @Query("SELECT COUNT(t) FROM Tratamiento t WHERE t.fecha >= :inicio")
    long contarDesde(@Param("inicio") LocalDate inicio);

    @Query("SELECT t.droga.nombre, COUNT(t) "
            + "FROM Tratamiento t "
            + "WHERE t.fecha >= :inicio "
            + "GROUP BY t.droga.id, t.droga.nombre "
            + "ORDER BY COUNT(t) DESC")
    List<Object[]> contarMedicamentosDesde(@Param("inicio") LocalDate inicio);

    @Query("SELECT t.droga.nombre, COUNT(t) "
            + "FROM Tratamiento t "
            + "GROUP BY t.droga.id, t.droga.nombre "
            + "ORDER BY COUNT(t) DESC")
    List<Object[]> contarTratamientosPorDroga();
}
