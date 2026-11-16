package com.vetopia.rest;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vetopia.dto.CitaDTO;
import com.vetopia.dto.ClasificacionMotivoCitaDTO;
import com.vetopia.dto.CrearCitaDTO;
import com.vetopia.dto.HorarioDisponibleDTO;
import com.vetopia.dto.SolicitudClasificacionMotivoDTO;
import com.vetopia.entities.EstadoCita;
import com.vetopia.service.cita.CitaService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/cita")
@CrossOrigin(origins = "http://localhost:4200")
public class CitaRestController {

    private final CitaService citaService;

    public CitaRestController(CitaService citaService) {
        this.citaService = citaService;
    }

    @Operation(summary = "Clasifica el motivo y recomienda una especialidad")
    @PostMapping("/clasificar-motivo")
    public ClasificacionMotivoCitaDTO clasificarMotivo(@RequestBody SolicitudClasificacionMotivoDTO solicitud) {
        return citaService.clasificarMotivo(solicitud == null ? null : solicitud.motivo());
    }

    @Operation(summary = "Lista las especialidades disponibles para agendar")
    @GetMapping("/especialidades")
    public List<String> listarEspecialidades() {
        return citaService.listarEspecialidadesDisponibles();
    }

    @Operation(summary = "Consulta horarios libres para una especialidad")
    @GetMapping("/disponibilidad")
    public List<HorarioDisponibleDTO> listarDisponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam int cantidadMascotas,
            @RequestParam String especialidad) {
        return citaService.listarDisponibilidad(fecha, cantidadMascotas, especialidad);
    }

    @Operation(summary = "Crea una cita y asigna un veterinario disponible")
    @PostMapping
    public CitaDTO crear(@RequestBody CrearCitaDTO solicitud) {
        return citaService.crear(solicitud);
    }

    @Operation(summary = "Lista las citas de un cliente")
    @GetMapping("/dueno/{duenoId}")
    public List<CitaDTO> listarDeDueno(@PathVariable Long duenoId) {
        return citaService.listarDeDueno(duenoId);
    }

    @Operation(summary = "Lista las citas de un veterinario en un rango de fechas")
    @GetMapping("/veterinario/{veterinarioId}")
    public List<CitaDTO> listarDeVeterinario(
            @PathVariable Long veterinarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return citaService.listarDeVeterinario(veterinarioId, desde, hasta);
    }

    @Operation(summary = "Lista todas las citas para la agenda administrativa")
    @GetMapping("/administracion")
    public List<CitaDTO> listarTodas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return citaService.listarTodas(desde, hasta);
    }

    @Operation(summary = "Cancela una cita por solicitud del cliente")
    @PutMapping("/{citaId}/cancelar")
    public CitaDTO cancelarDeDueno(@PathVariable Long citaId, @RequestParam Long duenoId) {
        return citaService.cancelarDeDueno(citaId, duenoId);
    }

    @Operation(summary = "Cancela una cita desde la agenda administrativa")
    @PutMapping("/{citaId}/cancelar-administrador")
    public CitaDTO cancelarComoAdministrador(@PathVariable Long citaId) {
        return citaService.cancelarComoAdministrador(citaId);
    }

    @Operation(summary = "Marca una cita como completada o inasistencia")
    @PutMapping("/{citaId}/estado")
    public CitaDTO actualizarEstadoVeterinario(
            @PathVariable Long citaId,
            @RequestParam Long veterinarioId,
            @RequestParam EstadoCita estado) {
        return citaService.actualizarEstadoVeterinario(citaId, veterinarioId, estado);
    }
}
