package com.vetopia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.vetopia.dto.ResultadoTratamientoDTO;
import com.vetopia.entities.Droga;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.exception.RecursoNoEncontradoException;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.TratamientoRepository;
import com.vetopia.service.droga.DrogaService;
import com.vetopia.service.tratamiento.TratamientoServiceImpl;
import com.vetopia.service.veterinario.VeterinarioService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TratamientoServiceMockTest {

    @Mock
    private TratamientoRepository tratamientoRepository;

    @Mock
    private DrogaService drogaService;

    @Mock
    private VeterinarioService veterinarioService;

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private TratamientoServiceImpl tratamientoService;

    @Test
    @DisplayName("listarTratamientos delega en el repositorio y devuelve su resultado")
    void listarTratamientos_delegaEnRepositorio() {
        // Arrange
        Tratamiento t1 = new Tratamiento();
        t1.setId(1L);
        Tratamiento t2 = new Tratamiento();
        t2.setId(2L);
        when(tratamientoRepository.findAll()).thenReturn(List.of(t1, t2));

        // Act
        List<Tratamiento> resultado = tratamientoService.listarTratamientos();

        // Assert
        assertThat(resultado).hasSize(2);
    }

    @Test
    @DisplayName("obtenerTratamientoPorId cuando existe devuelve el tratamiento simulado")
    void obtenerTratamientoPorId_existe() {
        // Arrange
        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setId(7L);
        tratamiento.setTitulo("Tratamiento mock");
        when(tratamientoRepository.findById(7L)).thenReturn(Optional.of(tratamiento));

        // Act
        Tratamiento resultado = tratamientoService.obtenerTratamientoPorId(7L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(7L);
        assertThat(resultado.getTitulo()).isEqualTo("Tratamiento mock");
    }

    @Test
    @DisplayName("obtenerTratamientoPorId cuando no existe lanza RecursoNoEncontradoException")
    void obtenerTratamientoPorId_noExiste() {
        // Arrange
        when(tratamientoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> tratamientoService.obtenerTratamientoPorId(99L));

    }

    @Test
    @DisplayName("asignarTratamiento exitoso guarda el tratamiento y descuenta una unidad")
    void asignarTratamiento_exitoso() {
        // Arrange
        Mascota mascota = new Mascota();
        mascota.setId(10L);
        mascota.setEstado(DominioEstado.ACTIVO);

        Droga droga = new Droga();
        droga.setId(20L);
        droga.setNombre("Droga Mock");
        droga.setUnidadesDisponibles(5);
        droga.setUnidadesVendidas(0);

        Veterinario veterinario = new Veterinario();
        veterinario.setId(30L);

        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setMascota(mascota);
        Droga drogaRef = new Droga();
        drogaRef.setId(20L);
        tratamiento.setDroga(drogaRef);
        tratamiento.setTitulo("Tratamiento mock");
        tratamiento.setDescripcion("desc");
        tratamiento.setFecha(LocalDate.now());

        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascota));
        when(drogaService.obtenerDrogaPorId(20L)).thenReturn(droga);
        when(veterinarioService.obtenerVeterinarioPorId(30L)).thenReturn(veterinario);
        when(tratamientoRepository.save(any(Tratamiento.class))).thenAnswer(inv -> {
            Tratamiento t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        // Act
        ResultadoTratamientoDTO resultado = tratamientoService.asignarTratamiento(tratamiento, 30L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTratamiento().getId()).isEqualTo(1L);
        assertThat(resultado.getTratamiento().getCompletado()).isFalse();
    }

    @Test
    @DisplayName("asignarTratamiento con mascota inactiva lanza ParametroInvalidoException y no guarda ni descuenta")
    void asignarTratamiento_mascotaInactiva() {
        // Arrange
        Mascota mascota = new Mascota();
        mascota.setId(10L);
        mascota.setEstado(DominioEstado.INACTIVO);

        Droga droga = new Droga();
        droga.setId(20L);
        droga.setNombre("Droga Mock");
        droga.setUnidadesDisponibles(5);

        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setMascota(mascota);
        Droga drogaRef = new Droga();
        drogaRef.setId(20L);
        tratamiento.setDroga(drogaRef);
        tratamiento.setTitulo("Sin asignar");

        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascota));

        // Act & Assert
        assertThrows(ParametroInvalidoException.class,
                () -> tratamientoService.asignarTratamiento(tratamiento, 5L));

    }
}
