package com.vetopia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vetopia.dto.CitaDTO;
import com.vetopia.dto.ClasificacionMotivoCitaDTO;
import com.vetopia.dto.CrearCitaDTO;
import com.vetopia.entities.Cita;
import com.vetopia.entities.CitaMascota;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.EstadoCita;
import com.vetopia.entities.Veterinario;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.entities.mascota.dominio_attr.DominioEspecie;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.entities.mascota.dominio_attr.DominioSexo;
import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.repository.CitaRepository;
import com.vetopia.repository.CitaMascotaRepository;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.VeterinarioRepository;
import com.vetopia.service.cita.CitaServiceImpl;
import com.vetopia.service.cita.ClasificadorMotivoCita;

@ExtendWith(MockitoExtension.class)
class CitaServiceMockTest {

    private static final String ESPECIALIDAD = "Medicina General";

    @Mock
    private CitaRepository citaRepository;
    @Mock
    private CitaMascotaRepository citaMascotaRepository;
    @Mock
    private DuenoRepository duenoRepository;
    @Mock
    private MascotaRepository mascotaRepository;
    @Mock
    private VeterinarioRepository veterinarioRepository;
    @Mock
    private ClasificadorMotivoCita clasificadorMotivoCita;

    @InjectMocks
    private CitaServiceImpl citaService;

    private Dueno dueno;
    private Veterinario veterinarioMenorCarga;
    private Veterinario veterinarioMayorCarga;
    private Mascota mascotaUno;
    private Mascota mascotaDos;

    @BeforeEach
    void setUp() {
        dueno = Dueno.builder().id(10L).nombre("Cliente de prueba").build();
        veterinarioMenorCarga = Veterinario.builder()
                .id(2L).nombre("Dra. Disponible").especialidad(ESPECIALIDAD).estado(DominioEstado.ACTIVO).build();
        veterinarioMayorCarga = Veterinario.builder()
                .id(1L).nombre("Dr. Ocupado").especialidad(ESPECIALIDAD).estado(DominioEstado.ACTIVO).build();
        mascotaUno = mascota(21L, "Luna");
        mascotaDos = mascota(22L, "Max");
    }

    @Test
    void crearCitaAgrupadaAsignaVeterinarioConMenorCargaYReservaDuracionContinua() {
        LocalDate fecha = proximoDiaLaboral();
        LocalDateTime inicio = fecha.atTime(14, 0);
        CrearCitaDTO solicitud = new CrearCitaDTO(
                dueno.getId(), List.of(mascotaUno.getId(), mascotaDos.getId()),
                "Consulta preventiva", ESPECIALIDAD, inicio);

        when(duenoRepository.findById(dueno.getId())).thenReturn(Optional.of(dueno));
        when(mascotaRepository.findAllById(solicitud.mascotaIds())).thenReturn(List.of(mascotaUno, mascotaDos));
        when(veterinarioRepository.listarEspecialidadesPorEstado(DominioEstado.ACTIVO))
                .thenReturn(List.of(ESPECIALIDAD));
        when(veterinarioRepository.findByEstadoAndEspecialidadIgnoreCase(DominioEstado.ACTIVO, ESPECIALIDAD))
                .thenReturn(List.of(veterinarioMayorCarga, veterinarioMenorCarga));
        when(citaRepository.countByVeterinario_IdAndFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThanAndEstado(
                eq(veterinarioMayorCarga.getId()), any(LocalDateTime.class), any(LocalDateTime.class),
                eq(EstadoCita.CONFIRMADA))).thenReturn(4L);
        when(citaRepository.countByVeterinario_IdAndFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThanAndEstado(
                eq(veterinarioMenorCarga.getId()), any(LocalDateTime.class), any(LocalDateTime.class),
                eq(EstadoCita.CONFIRMADA))).thenReturn(1L);
        when(veterinarioRepository.buscarPorIdConBloqueo(veterinarioMenorCarga.getId()))
                .thenReturn(Optional.of(veterinarioMenorCarga));
        when(citaRepository.existeSolapamiento(veterinarioMenorCarga.getId(), inicio, inicio.plusMinutes(60),
                EstadoCita.CONFIRMADA)).thenReturn(false);
        when(citaRepository.saveAndFlush(any(Cita.class))).thenAnswer(invocation -> {
            Cita cita = invocation.getArgument(0);
            cita.setId(55L);
            return cita;
        });
        when(citaMascotaRepository.findByCita_Id(55L)).thenReturn(List.of(
                CitaMascota.builder().mascota(mascotaUno).build(),
                CitaMascota.builder().mascota(mascotaDos).build()));

        CitaDTO resultado = citaService.crear(solicitud);

        assertEquals(veterinarioMenorCarga.getId(), resultado.veterinarioId());
        assertEquals(inicio, resultado.fechaHoraInicio());
        assertEquals(inicio.plusMinutes(60), resultado.fechaHoraFin());
        assertEquals(2, resultado.mascotas().size());
        assertEquals(EstadoCita.CONFIRMADA, resultado.estado());
    }

    @Test
    void disponibilidadNoIncluyeDomingosNiCruzaElAlmuerzo() {
        LocalDate fecha = proximoDiaLaboral();
        when(veterinarioRepository.listarEspecialidadesPorEstado(DominioEstado.ACTIVO))
                .thenReturn(List.of(ESPECIALIDAD));
        when(veterinarioRepository.findByEstadoAndEspecialidadIgnoreCase(DominioEstado.ACTIVO, ESPECIALIDAD))
                .thenReturn(List.of(veterinarioMenorCarga));

        var horarios = citaService.listarDisponibilidad(fecha, 2, ESPECIALIDAD);

        assertTrue(horarios.stream().allMatch(horario ->
                !horario.fechaHoraInicio().toLocalTime().isBefore(java.time.LocalTime.of(14, 0))
                        || !horario.fechaHoraFin().toLocalTime().isAfter(java.time.LocalTime.of(13, 0))));
        assertTrue(horarios.stream().noneMatch(horario -> horario.fechaHoraInicio().getHour() == 13));

        LocalDate domingo = fecha.with(DayOfWeek.SUNDAY);
        assertThrows(ParametroInvalidoException.class,
                () -> citaService.listarDisponibilidad(domingo, 1, ESPECIALIDAD));
    }

    @Test
    void siElClasificadorNoRespondeOfreceSeleccionManual() {
        when(veterinarioRepository.listarEspecialidadesPorEstado(DominioEstado.ACTIVO))
                .thenReturn(List.of(ESPECIALIDAD, "Dermatología"));
        when(clasificadorMotivoCita.clasificar("Le duele una pata", List.of(ESPECIALIDAD, "Dermatología")))
                .thenReturn(Optional.empty());

        ClasificacionMotivoCitaDTO resultado = citaService.clasificarMotivo("Le duele una pata");

        assertTrue(resultado.requiereSeleccionManual());
        assertNull(resultado.especialidad());
        assertEquals(2, resultado.especialidadesDisponibles().size());
    }

    private Mascota mascota(Long id, String nombre) {
        return Mascota.builder()
                .id(id)
                .nombre(nombre)
                .especie(DominioEspecie.PERRO)
                .raza("Criollo")
                .edad(3.0)
                .sexo(DominioSexo.MACHO)
                .pesoKg(10.0)
                .color("Café")
                .fechaIngreso(LocalDate.now())
                .estado(DominioEstado.ACTIVO)
                .dueno(dueno)
                .build();
    }

    private LocalDate proximoDiaLaboral() {
        LocalDate fecha = LocalDate.now(ZoneId.of("America/Bogota")).plusDays(1);
        while (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) fecha = fecha.plusDays(1);
        return fecha;
    }
}
