package com.vetopia.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.entities.mascota.dominio_attr.DominioEspecie;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.entities.mascota.dominio_attr.DominioSexo;

@DataJpaTest
public class TratamientoRepositoryTest {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private DrogaRepository drogaRepository;

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @BeforeEach
    void init() {
        // 1. Crear las entidades relacionadas necesarias para los tratamientos.
        Dueno dueno = duenoRepository.save(Dueno.builder()
                .cedula("4001")
                .nombre("Dueno Tratamientos")
                .correo("tratamientos@vetopia.com")
                .contrasena("clave123")
                .celular("3000000010")
                .build());

        Mascota mascota = mascotaRepository.save(new Mascota(
                null, "Firulais", DominioEspecie.PERRO, "Mestizo", 2.0,
                DominioSexo.MACHO, null, 8.5, "Cafe", LocalDate.of(2025, 1, 1),
                dueno, DominioEstado.ACTIVO, new ArrayList<>()));

        Veterinario veterinario = veterinarioRepository.save(new Veterinario(
                "5001", "clave123", "veterinario@vetopia.com", "Cirugia", 0,
                "Veterinario de Prueba", DominioEstado.ACTIVO, null));

        Droga antibiotico = drogaRepository.save(new Droga(
                "Antibiotico", 10.0, 20.0, 100, 0));
        Droga analgesico = drogaRepository.save(new Droga(
                "Analgesico", 5.0, 8.0, 100, 0));

        // 2. Crear tres tratamientos: dos recientes con el mismo medicamento
        // y uno anterior con un medicamento diferente.
        tratamientoRepository.save(new Tratamiento(
                LocalDate.of(2026, 1, 10), mascota, veterinario, antibiotico,
                "Tratamiento antibiotico 1", "Descripcion 1", true));
        tratamientoRepository.save(new Tratamiento(
                LocalDate.of(2026, 2, 10), mascota, veterinario, antibiotico,
                "Tratamiento antibiotico 2", "Descripcion 2", true));
        tratamientoRepository.save(new Tratamiento(
                LocalDate.of(2025, 12, 15), mascota, veterinario, analgesico,
                "Tratamiento analgesico", "Descripcion 3", true));
    }

    @Test
    public void TratamientoRepository_sumVentasAplicadas_ReturnsTotalVentas() {
        // Act
        double resultado = tratamientoRepository.sumVentasAplicadas();

        // Assert: (20 * 2) + 8
        Assertions.assertThat(resultado).isEqualTo(48.0);
    }

    @Test
    public void TratamientoRepository_sumGananciasAplicadas_ReturnsTotalGanancias() {
        // Act
        double resultado = tratamientoRepository.sumGananciasAplicadas();

        // Assert: ((20 - 10) * 2) + (8 - 5)
        Assertions.assertThat(resultado).isEqualTo(23.0);
    }

    @Test
    public void TratamientoRepository_contarDesde_ReturnsTreatmentsFromDate() {
        // Arrange
        LocalDate inicio = LocalDate.of(2026, 1, 1);

        // Act
        long resultado = tratamientoRepository.contarDesde(inicio);

        // Assert: solo se incluyen los tratamientos del 10 de enero y 10 de febrero.
        Assertions.assertThat(resultado).isEqualTo(2);
    }

    @Test
    public void TratamientoRepository_contarMedicamentosDesde_ReturnsGroupedResultsOrdered() {
        // Arrange
        LocalDate inicio = LocalDate.of(2026, 1, 1);

        // Act
        List<Object[]> resultado = tratamientoRepository.contarMedicamentosDesde(inicio);

        // Assert
        Assertions.assertThat(resultado.size()).isEqualTo(1);
        Assertions.assertThat(resultado.get(0)[0]).isEqualTo("Antibiotico");
        Assertions.assertThat(resultado.get(0)[1]).isEqualTo(2L);
    }

    @Test
    public void TratamientoRepository_contarTratamientosPorDroga_ReturnsGroupedResultsOrdered() {
        // Act
        List<Object[]> resultado = tratamientoRepository.contarTratamientosPorDroga();

        // Assert: el antibiotico aparece dos veces y debe quedar primero.
        Assertions.assertThat(resultado.size()).isEqualTo(2);
        Assertions.assertThat(resultado.get(0)[0]).isEqualTo("Antibiotico");
        Assertions.assertThat(resultado.get(0)[1]).isEqualTo(2L);
        Assertions.assertThat(resultado.get(1)[0]).isEqualTo("Analgesico");
        Assertions.assertThat(resultado.get(1)[1]).isEqualTo(1L);
    }
}
