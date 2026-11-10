package com.vetopia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.vetopia.dto.CitaDTO;
import com.vetopia.dto.CrearCitaDTO;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.EstadoCita;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.repository.CitaRepository;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.service.cita.CitaService;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class CitaServiceIntegrationTest {

    @Autowired
    private CitaService citaService;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Test
    void dataLoaderCreaCitasDeDemostracion() {
        assertThat(citaRepository.count()).isGreaterThan(0);
        assertThat(citaService.listarEspecialidadesDisponibles()).isNotEmpty();
    }

    @Test
    void reservaCitaAgrupadaYRechazaElSolapamiento() {
        Mascota mascota = mascotaRepository.findAll().stream()
                .filter(item -> item.getDueno() != null)
                .findFirst()
                .orElseThrow();
        Dueno dueno = mascota.getDueno();
        String especialidad = citaService.listarEspecialidadesDisponibles().get(0);
        LocalDate fecha = buscarFechaConDisponibilidad(especialidad);
        var horario = citaService.listarDisponibilidad(fecha, 1, especialidad).get(0);

        CrearCitaDTO solicitud = new CrearCitaDTO(
                dueno.getId(), List.of(mascota.getId()), "Control preventivo", especialidad,
                horario.fechaHoraInicio());

        CitaDTO cita = citaService.crear(solicitud);

        assertThat(cita.id()).isNotNull();
        assertThat(cita.estado()).isEqualTo(EstadoCita.CONFIRMADA);
        assertThat(cita.veterinarioEspecialidad()).isEqualToIgnoringCase(especialidad);
        assertThat(cita.fechaHoraFin()).isEqualTo(horario.fechaHoraInicio().plusMinutes(30));
        assertThat(cita.mascotas()).extracting("id").containsExactly(mascota.getId());

        assertThrows(ParametroInvalidoException.class, () -> citaService.crear(solicitud));
    }

    private LocalDate buscarFechaConDisponibilidad(String especialidad) {
        LocalDate fecha = LocalDate.now(ZoneId.of("America/Bogota")).plusDays(1);
        for (int intento = 0; intento < 21; intento++, fecha = fecha.plusDays(1)) {
            if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) continue;
            if (!citaService.listarDisponibilidad(fecha, 1, especialidad).isEmpty()) return fecha;
        }
        throw new AssertionError("No se encontró una fecha de prueba con disponibilidad.");
    }
}
