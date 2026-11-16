package com.vetopia.service.cita;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vetopia.dto.CitaDTO;
import com.vetopia.dto.ClasificacionMotivoCitaDTO;
import com.vetopia.dto.CrearCitaDTO;
import com.vetopia.dto.HorarioDisponibleDTO;
import com.vetopia.dto.MascotaCitaDTO;
import com.vetopia.entities.Cita;
import com.vetopia.entities.CitaMascota;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.EstadoCita;
import com.vetopia.entities.Veterinario;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.exception.RecursoNoEncontradoException;
import com.vetopia.repository.CitaMascotaRepository;
import com.vetopia.repository.CitaRepository;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.VeterinarioRepository;

@Service
public class CitaServiceImpl implements CitaService {

    private static final ZoneId ZONA_CLINICA = ZoneId.of("America/Bogota");
    private static final LocalTime APERTURA_MANANA = LocalTime.of(9, 0);
    private static final LocalTime INICIO_ALMUERZO = LocalTime.of(13, 0);
    private static final LocalTime FIN_ALMUERZO = LocalTime.of(14, 0);
    private static final LocalTime CIERRE = LocalTime.of(18, 0);
    private static final int MINUTOS_POR_MASCOTA = 30;
    private static final int MAX_MASCOTAS_POR_CITA = 5;

    private final CitaRepository citaRepository;
    private final CitaMascotaRepository citaMascotaRepository;
    private final DuenoRepository duenoRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final ClasificadorMotivoCita clasificadorMotivoCita;

    public CitaServiceImpl(CitaRepository citaRepository,
            CitaMascotaRepository citaMascotaRepository,
            DuenoRepository duenoRepository,
            MascotaRepository mascotaRepository,
            VeterinarioRepository veterinarioRepository,
            ClasificadorMotivoCita clasificadorMotivoCita) {
        this.citaRepository = citaRepository;
        this.citaMascotaRepository = citaMascotaRepository;
        this.duenoRepository = duenoRepository;
        this.mascotaRepository = mascotaRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.clasificadorMotivoCita = clasificadorMotivoCita;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarEspecialidadesDisponibles() {
        return veterinarioRepository.listarEspecialidadesPorEstado(DominioEstado.ACTIVO);
    }

    @Override
    @Transactional(readOnly = true)
    public ClasificacionMotivoCitaDTO clasificarMotivo(String motivo) {
        validarMotivo(motivo);
        List<String> especialidades = listarEspecialidadesDisponibles();
        if (especialidades.isEmpty()) {
            throw new ParametroInvalidoException("No hay veterinarios activos para asignar citas.");
        }

        return clasificadorMotivoCita.clasificar(motivo.trim(), especialidades)
                .map(especialidad -> new ClasificacionMotivoCitaDTO(
                        especialidad, false, especialidades, "Motivo asociado a una especialidad."))
                .orElseGet(() -> new ClasificacionMotivoCitaDTO(
                        null, true, especialidades,
                        "No se pudo clasificar el motivo automáticamente. Selecciona una especialidad."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioDisponibleDTO> listarDisponibilidad(LocalDate fecha, int cantidadMascotas,
            String especialidad) {
        validarFechaYCantidad(fecha, cantidadMascotas);
        validarEspecialidad(especialidad);

        List<Veterinario> veterinarios = veterinarioRepository
                .findByEstadoAndEspecialidadIgnoreCase(DominioEstado.ACTIVO, especialidad.trim());
        if (veterinarios.isEmpty()) {
            return List.of();
        }

        int duracionMinutos = cantidadMascotas * MINUTOS_POR_MASCOTA;
        List<HorarioDisponibleDTO> disponibles = new ArrayList<>();
        for (LocalTime hora = APERTURA_MANANA; !hora.isAfter(CIERRE.minusMinutes(30)); hora = hora.plusMinutes(30)) {
            LocalDateTime inicio = fecha.atTime(hora);
            LocalDateTime fin = inicio.plusMinutes(duracionMinutos);
            if (!cabeEnJornada(inicio, fin) || inicio.isBefore(LocalDateTime.now(ZONA_CLINICA))) {
                continue;
            }

            boolean hayVeterinario = veterinarios.stream()
                    .anyMatch(veterinario -> !citaRepository.existeSolapamiento(
                            veterinario.getId(), inicio, fin, EstadoCita.CONFIRMADA));
            if (hayVeterinario) {
                disponibles.add(new HorarioDisponibleDTO(inicio, fin));
            }
        }
        return disponibles;
    }

    @Override
    @Transactional
    public CitaDTO crear(CrearCitaDTO solicitud) {
        validarSolicitud(solicitud);

        Dueno dueno = duenoRepository.findById(solicitud.duenoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el cliente."));
        List<Mascota> mascotas = cargarMascotasDelDueno(solicitud.mascotaIds(), dueno.getId());
        int duracionMinutos = mascotas.size() * MINUTOS_POR_MASCOTA;
        LocalDateTime inicio = solicitud.fechaHoraInicio();
        LocalDateTime fin = inicio.plusMinutes(duracionMinutos);

        validarFechaYCantidad(inicio.toLocalDate(), mascotas.size());
        validarEspecialidad(solicitud.especialidad());
        validarEnHorario(inicio, fin);
        if (inicio.isBefore(LocalDateTime.now(ZONA_CLINICA))) {
            throw new ParametroInvalidoException("La fecha y hora de la cita deben estar en el futuro.");
        }

        List<Veterinario> candidatos = new ArrayList<>(
                veterinarioRepository.findByEstadoAndEspecialidadIgnoreCase(
                        DominioEstado.ACTIVO, solicitud.especialidad().trim()));
        candidatos.sort(Comparator
                .comparingLong((Veterinario veterinario) -> citaRepository
                        .countByVeterinario_IdAndFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThanAndEstado(
                                veterinario.getId(), inicio.toLocalDate().atStartOfDay(),
                                inicio.toLocalDate().plusDays(1).atStartOfDay(), EstadoCita.CONFIRMADA))
                .thenComparing(Veterinario::getId));

        for (Veterinario candidato : candidatos) {
            // Serializa las reservas concurrentes que intentan usar el mismo veterinario.
            Veterinario bloqueado = veterinarioRepository.buscarPorIdConBloqueo(candidato.getId()).orElse(null);
            if (bloqueado == null || bloqueado.getEstado() != DominioEstado.ACTIVO
                    || !bloqueado.getEspecialidad().equalsIgnoreCase(solicitud.especialidad().trim())) {
                continue;
            }
            if (citaRepository.existeSolapamiento(bloqueado.getId(), inicio, fin, EstadoCita.CONFIRMADA)) {
                continue;
            }

            Cita cita = citaRepository.saveAndFlush(Cita.builder()
                    .dueno(dueno)
                    .veterinario(bloqueado)
                    .fechaHoraInicio(inicio)
                    .fechaHoraFin(fin)
                    .motivo(solicitud.motivo().trim())
                    .especialidad(bloqueado.getEspecialidad())
                    .estado(EstadoCita.CONFIRMADA)
                    .creadaEn(LocalDateTime.now(ZONA_CLINICA))
                    .build());
            citaMascotaRepository.saveAll(mascotas.stream()
                    .map(mascota -> CitaMascota.builder().cita(cita).mascota(mascota).build())
                    .toList());
            return convertir(cita);
        }

        throw new ParametroInvalidoException(
                "Ese horario acaba de ocuparse. Selecciona otro horario disponible.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaDTO> listarDeDueno(Long duenoId) {
        if (duenoId == null) {
            throw new ParametroInvalidoException("No se especificó el identificador del cliente.");
        }
        if (!duenoRepository.existsById(duenoId)) {
            throw new RecursoNoEncontradoException("No se encontró el cliente.");
        }
        return citaRepository.findByDueno_IdOrderByFechaHoraInicioDesc(duenoId)
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaDTO> listarDeVeterinario(Long veterinarioId, LocalDate desde, LocalDate hasta) {
        validarRango(veterinarioId, desde, hasta);
        if (!veterinarioRepository.existsById(veterinarioId)) {
            throw new RecursoNoEncontradoException("No se encontró el veterinario.");
        }
        return citaRepository
                .findByVeterinario_IdAndFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThanOrderByFechaHoraInicioAsc(
                        veterinarioId, desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay())
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaDTO> listarTodas(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);
        return citaRepository
                .findByFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThanOrderByFechaHoraInicioAsc(
                        desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay())
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional
    public CitaDTO cancelarDeDueno(Long citaId, Long duenoId) {
        Cita cita = obtenerCita(citaId);
        validarDuenoDeCita(cita, duenoId);
        if (cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new ParametroInvalidoException("Solo se pueden cancelar citas confirmadas.");
        }
        if (cita.getFechaHoraInicio().isBefore(LocalDateTime.now(ZONA_CLINICA).plusHours(24))) {
            throw new ParametroInvalidoException("La cita solo se puede cancelar hasta 24 horas antes.");
        }
        cita.setEstado(EstadoCita.CANCELADA);
        return convertir(cita);
    }

    @Override
    @Transactional
    public CitaDTO cancelarComoAdministrador(Long citaId) {
        Cita cita = obtenerCita(citaId);
        if (cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new ParametroInvalidoException("Solo se pueden cancelar citas confirmadas.");
        }
        cita.setEstado(EstadoCita.CANCELADA);
        return convertir(cita);
    }

    @Override
    @Transactional
    public CitaDTO actualizarEstadoVeterinario(Long citaId, Long veterinarioId, EstadoCita estado) {
        Cita cita = obtenerCita(citaId);
        if (veterinarioId == null || !veterinarioId.equals(cita.getVeterinario().getId())) {
            throw new ParametroInvalidoException("Esta cita no está asignada a este veterinario.");
        }
        if (estado != EstadoCita.COMPLETADA && estado != EstadoCita.INASISTENCIA) {
            throw new ParametroInvalidoException("El veterinario solo puede marcar una cita como completada o inasistencia.");
        }
        if (cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new ParametroInvalidoException("Solo se puede actualizar una cita confirmada.");
        }
        cita.setEstado(estado);
        return convertir(cita);
    }

    private List<Mascota> cargarMascotasDelDueno(List<Long> mascotaIds, Long duenoId) {
        if (mascotaIds == null || mascotaIds.isEmpty() || mascotaIds.size() > MAX_MASCOTAS_POR_CITA) {
            throw new ParametroInvalidoException("Selecciona entre 1 y " + MAX_MASCOTAS_POR_CITA + " mascotas.");
        }
        Set<Long> idsUnicos = new HashSet<>(mascotaIds);
        if (idsUnicos.size() != mascotaIds.size() || idsUnicos.contains(null)) {
            throw new ParametroInvalidoException("La selección de mascotas contiene ids repetidos o inválidos.");
        }

        List<Mascota> mascotas = mascotaRepository.findAllById(mascotaIds);
        if (mascotas.size() != mascotaIds.size()) {
            throw new RecursoNoEncontradoException("No se encontró una de las mascotas seleccionadas.");
        }
        boolean todasPertenecenAlDueno = mascotas.stream()
                .allMatch(mascota -> mascota.getDueno() != null && duenoId.equals(mascota.getDueno().getId()));
        if (!todasPertenecenAlDueno) {
            throw new ParametroInvalidoException("Todas las mascotas deben pertenecer al cliente indicado.");
        }
        return mascotas;
    }

    private void validarSolicitud(CrearCitaDTO solicitud) {
        if (solicitud == null || solicitud.duenoId() == null || solicitud.fechaHoraInicio() == null) {
            throw new ParametroInvalidoException("Faltan datos obligatorios para crear la cita.");
        }
        validarMotivo(solicitud.motivo());
        if (solicitud.especialidad() == null || solicitud.especialidad().isBlank()) {
            throw new ParametroInvalidoException("Selecciona una especialidad para la cita.");
        }
    }

    private void validarMotivo(String motivo) {
        if (motivo == null || motivo.isBlank() || motivo.trim().length() > 1000) {
            throw new ParametroInvalidoException("Describe el motivo de la cita (máximo 1000 caracteres).");
        }
    }

    private void validarEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.isBlank()) {
            throw new ParametroInvalidoException("Selecciona una especialidad para la cita.");
        }
        boolean existe = veterinarioRepository.listarEspecialidadesPorEstado(DominioEstado.ACTIVO).stream()
                .anyMatch(disponible -> disponible.equalsIgnoreCase(especialidad.trim()));
        if (!existe) {
            throw new ParametroInvalidoException("La especialidad seleccionada no está disponible.");
        }
    }

    private void validarFechaYCantidad(LocalDate fecha, int cantidadMascotas) {
        if (fecha == null || fecha.isBefore(LocalDate.now(ZONA_CLINICA))) {
            throw new ParametroInvalidoException("Selecciona una fecha futura o el día de hoy.");
        }
        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new ParametroInvalidoException("La clínica no atiende los domingos.");
        }
        if (cantidadMascotas < 1 || cantidadMascotas > MAX_MASCOTAS_POR_CITA) {
            throw new ParametroInvalidoException("Selecciona entre 1 y " + MAX_MASCOTAS_POR_CITA + " mascotas.");
        }
    }

    private void validarEnHorario(LocalDateTime inicio, LocalDateTime fin) {
        if (!cabeEnJornada(inicio, fin)) {
            throw new ParametroInvalidoException(
                    "El horario debe estar entre 9:00 y 13:00 o entre 14:00 y 18:00, en intervalos de 30 minutos.");
        }
    }

    private boolean cabeEnJornada(LocalDateTime inicio, LocalDateTime fin) {
        LocalTime horaInicio = inicio.toLocalTime();
        LocalTime horaFin = fin.toLocalTime();
        boolean jornadaManana = !horaInicio.isBefore(APERTURA_MANANA) && !horaFin.isAfter(INICIO_ALMUERZO);
        boolean jornadaTarde = !horaInicio.isBefore(FIN_ALMUERZO) && !horaFin.isAfter(CIERRE);
        boolean enIntervaloDe30Minutos = horaInicio.getMinute() % 30 == 0
                && horaInicio.getSecond() == 0 && horaInicio.getNano() == 0;
        return (jornadaManana || jornadaTarde) && enIntervaloDe30Minutos;
    }

    private void validarRango(Long veterinarioId, LocalDate desde, LocalDate hasta) {
        if (veterinarioId == null) {
            throw new ParametroInvalidoException("No se especificó el identificador del veterinario.");
        }
        validarRango(desde, hasta);
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || hasta.isBefore(desde) || hasta.isAfter(desde.plusDays(31))) {
            throw new ParametroInvalidoException("El rango debe ser válido y no superar 32 días.");
        }
    }

    private Cita obtenerCita(Long citaId) {
        if (citaId == null) {
            throw new ParametroInvalidoException("No se especificó el identificador de la cita.");
        }
        return citaRepository.findById(citaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la cita."));
    }

    private void validarDuenoDeCita(Cita cita, Long duenoId) {
        if (duenoId == null || !duenoId.equals(cita.getDueno().getId())) {
            throw new ParametroInvalidoException("Esta cita no pertenece al cliente indicado.");
        }
    }

    private CitaDTO convertir(Cita cita) {
        List<MascotaCitaDTO> mascotas = citaMascotaRepository.findByCita_Id(cita.getId()).stream()
                .map(relacion -> relacion.getMascota())
                .map(mascota -> new MascotaCitaDTO(
                        mascota.getId(), mascota.getNombre(), mascota.getEspecie().toString(), mascota.getRaza()))
                .toList();
        return new CitaDTO(
                cita.getId(),
                cita.getDueno().getId(),
                cita.getDueno().getNombre(),
                cita.getVeterinario().getId(),
                cita.getVeterinario().getNombre(),
                cita.getVeterinario().getEspecialidad(),
                mascotas,
                cita.getFechaHoraInicio(),
                cita.getFechaHoraFin(),
                cita.getMotivo(),
                cita.getEspecialidad(),
                cita.getEstado());
    }
}
