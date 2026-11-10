package com.vetopia.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vetopia.entities.Cita;
import com.vetopia.entities.EstadoCita;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByDueno_IdOrderByFechaHoraInicioDesc(Long duenoId);

    List<Cita> findByVeterinario_IdAndFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThanOrderByFechaHoraInicioAsc(
            Long veterinarioId, LocalDateTime desde, LocalDateTime hasta);

    List<Cita> findByFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThanOrderByFechaHoraInicioAsc(
            LocalDateTime desde, LocalDateTime hasta);

    long countByVeterinario_IdAndFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThanAndEstado(
            Long veterinarioId, LocalDateTime desde, LocalDateTime hasta, EstadoCita estado);

    @Query("select case when count(c) > 0 then true else false end from Cita c "
            + "where c.veterinario.id = :veterinarioId and c.estado = :estado "
            + "and c.fechaHoraInicio < :fin and c.fechaHoraFin > :inicio")
    boolean existeSolapamiento(@Param("veterinarioId") Long veterinarioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("estado") EstadoCita estado);
}
