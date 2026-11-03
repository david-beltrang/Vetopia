package com.vetopia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.vetopia.dto.ResultadoTratamientoDTO;
import com.vetopia.entities.Droga;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.entities.mascota.dominio_attr.DominioEspecie;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.entities.mascota.dominio_attr.DominioSexo;
import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.repository.DrogaRepository;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.TratamientoRepository;
import com.vetopia.repository.VeterinarioRepository;
import com.vetopia.service.tratamiento.TratamientoService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class TratamientoServiceIntegrationTest {

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private DrogaRepository drogaRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    private Dueno dueno;
    private Mascota mascotaActiva;
    private Veterinario veterinario;
    private Droga droga;

    @BeforeEach
    void setUp() {
        // Arrange: datos base reutilizados por las pruebas
        dueno = duenoRepository.save(Dueno.builder()
                .cedula("9999999999")
                .nombre("Dueno Test")
                .correo("dueno.test@vetopia.com")
                .contrasena("dueno123")
                .celular("3999999999")
                .build());

        mascotaActiva = mascotaRepository.save(Mascota.builder()
                .nombre("Mascota Test")
                .especie(DominioEspecie.PERRO)
                .raza("Criollo")
                .edad(3.0)
                .sexo(DominioSexo.MACHO)
                .pesoKg(10.0)
                .color("Cafe")
                .fechaIngreso(LocalDate.now())
                .dueno(dueno)
                .estado(DominioEstado.ACTIVO)
                .build());

        veterinario = veterinarioRepository.save(Veterinario.builder()
                .cedula("8888888888")
                .nombre("Veterinario Test")
                .correo("vet.test@vetopia.com")
                .especialidad("Medicina General")
                .contrasena("vet123")
                .numeroAtenciones(0)
                .estado(DominioEstado.ACTIVO)
                .build());

        droga = drogaRepository.save(Droga.builder()
                .nombre("Droga Test")
                .precioCompra(10.0)
                .precioVenta(20.0)
                .unidadesDisponibles(5)
                .unidadesVendidas(0)
                .build());
    }

    @Test
    @DisplayName("asignarTratamiento persiste el tratamiento, descuenta stock y devuelve DTO")
    void asignarTratamiento_exitoso() {
        // Arrange
        Tratamiento tratamiento = Tratamiento.builder()
                .fecha(LocalDate.now())
                .mascota(mascotaActiva)
                .droga(droga)
                .titulo("Tratamiento de prueba")
                .descripcion("Descripcion de prueba")
                .completado(false)
                .build();

        // Act
        ResultadoTratamientoDTO resultado = tratamientoService.asignarTratamiento(tratamiento, veterinario.getId());

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTratamiento()).isNotNull();
        assertThat(resultado.getTratamiento().getId()).isNotNull();
        assertThat(resultado.getTratamiento().getCompletado()).isFalse();
        assertThat(resultado.getTratamiento().getVeterinario().getId()).isEqualTo(veterinario.getId());
        assertThat(resultado.getMascota().getId()).isEqualTo(mascotaActiva.getId());
        assertThat(resultado.getDroga().getId()).isEqualTo(droga.getId());

        Droga drogaActualizada = drogaRepository.findById(droga.getId()).orElseThrow();
        assertThat(drogaActualizada.getUnidadesDisponibles()).isEqualTo(4);
        assertThat(drogaActualizada.getUnidadesVendidas()).isEqualTo(1);

        List<Tratamiento> tratamientos = tratamientoRepository.findAll();
        assertThat(tratamientos.stream().anyMatch(t -> t.getId().equals(resultado.getTratamiento().getId()))).isTrue();
    }

    @Test
    @DisplayName("asignarTratamiento a mascota inactiva lanza ParametroInvalidoException y no descuenta stock")
    void asignarTratamiento_mascotaInactiva() {
        // Arrange
        Mascota mascotaInactiva = mascotaRepository.save(Mascota.builder()
                .nombre("Mascota Inactiva")
                .especie(DominioEspecie.PERRO)
                .raza("Criollo")
                .edad(3.0)
                .sexo(DominioSexo.MACHO)
                .pesoKg(10.0)
                .color("Cafe")
                .fechaIngreso(LocalDate.now())
                .dueno(dueno)
                .estado(DominioEstado.INACTIVO)
                .build());

        Tratamiento tratamiento = Tratamiento.builder()
                .fecha(LocalDate.now())
                .mascota(mascotaInactiva)
                .droga(droga)
                .titulo("Tratamiento inactivo")
                .descripcion("No debe asignarse")
                .build();

        // Act & Assert
        assertThrows(ParametroInvalidoException.class,
                () -> tratamientoService.asignarTratamiento(tratamiento, veterinario.getId()));

        Droga drogaActualizada = drogaRepository.findById(droga.getId()).orElseThrow();
        assertThat(drogaActualizada.getUnidadesDisponibles()).isEqualTo(5);
        assertThat(drogaActualizada.getUnidadesVendidas()).isEqualTo(0);
    }

    @Test
    @DisplayName("listarTratamientosPorVeterinario devuelve solo los tratamientos del veterinario indicado")
    void listarTratamientosPorVeterinario_filtra() {
        // Arrange
        Veterinario veterinarioB = veterinarioRepository.save(Veterinario.builder()
                .cedula("7777777777")
                .nombre("Veterinario B")
                .correo("vetb.test@vetopia.com")
                .especialidad("Cirugia")
                .contrasena("vet123")
                .numeroAtenciones(0)
                .estado(DominioEstado.ACTIVO)
                .build());

        Tratamiento t1 = Tratamiento.builder()
                .fecha(LocalDate.now())
                .mascota(mascotaActiva)
                .veterinario(veterinario)
                .droga(droga)
                .titulo("T1")
                .descripcion("d1")
                .completado(false)
                .build();
        tratamientoRepository.save(t1);

        Tratamiento t2 = Tratamiento.builder()
                .fecha(LocalDate.now())
                .mascota(mascotaActiva)
                .veterinario(veterinarioB)
                .droga(droga)
                .titulo("T2")
                .descripcion("d2")
                .completado(false)
                .build();
        tratamientoRepository.save(t2);

        // Act
        List<Tratamiento> delVeterinarioA = tratamientoService.listarTratamientosPorVeterinario(veterinario.getId());

        // Assert
        assertThat(delVeterinarioA).hasSize(1);
        assertThat(delVeterinarioA.get(0).getTitulo()).isEqualTo("T1");
    }

    @Test
    @DisplayName("actualizarEstado cambia el estado completado del tratamiento y lo persiste")
    void actualizarEstado_cambiaCompletado() {
        // Arrange
        Tratamiento tratamiento = Tratamiento.builder()
                .fecha(LocalDate.now())
                .mascota(mascotaActiva)
                .veterinario(veterinario)
                .droga(droga)
                .titulo("T estado")
                .descripcion("desc")
                .completado(false)
                .build();
        tratamiento = tratamientoRepository.save(tratamiento);

        // Act
        Tratamiento actualizado = tratamientoService.actualizarEstado(tratamiento.getId(), true);

        // Assert
        assertThat(actualizado.getCompletado()).isTrue();
        Tratamiento enBD = tratamientoRepository.findById(tratamiento.getId()).orElseThrow();
        assertThat(enBD.getCompletado()).isTrue();

        // Act (volver a pendiente)
        Tratamiento devuelto = tratamientoService.actualizarEstado(tratamiento.getId(), false);

        // Assert
        assertThat(devuelto.getCompletado()).isFalse();
    }
}
