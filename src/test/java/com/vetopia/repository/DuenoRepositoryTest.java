package com.vetopia.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vetopia.entities.Dueno;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.entities.mascota.dominio_attr.DominioEspecie;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.entities.mascota.dominio_attr.DominioSexo;

@DataJpaTest
public class DuenoRepositoryTest {

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @BeforeEach
    void init() {
        // 1. Crear y guardar 4 dueños (variables locales, sin campos globales)
        // Constructor: id, cedula, nombre, correo, contrasena, celular, mascotas
        Dueno dueno1 = duenoRepository.save(new Dueno(
                null, "3001", "Dueno 3001", "dueno1@vetopia.com", "clave123",
                "3000000001", new ArrayList<>()));

        Dueno dueno2 = duenoRepository.save(new Dueno(
                null, "3002", "Dueno 3002", "dueno2@vetopia.com", "clave123",
                "3000000002", new ArrayList<>()));

        duenoRepository.save(new Dueno(
                null, "3003", "Dueno 3003", "dueno3@vetopia.com", "clave123",
                "3000000003", new ArrayList<>()));

        duenoRepository.save(new Dueno(
                null, "3004", "Dueno 3004", "dueno4@vetopia.com", "clave123",
                "3000000004", new ArrayList<>()));

        // 2. Se crean 3 mascotas y se les asigna el dueño con setDueno()
        //    (2 mascotas para el dueño 1 y 1 mascota para el dueño 2)
        Mascota mascota1 = new Mascota(
                null, "Firulais", DominioEspecie.PERRO, "Mestizo", 2.0,
                DominioSexo.MACHO, null, 8.5, "Cafe", LocalDate.now(), null,
                DominioEstado.ACTIVO, new ArrayList<>());
        mascota1.setDueno(dueno1);
        dueno1.getMascotas().add(mascota1);
        mascotaRepository.save(mascota1);

        Mascota mascota2 = new Mascota(
                null, "Michi", DominioEspecie.GATO, "Mestizo", 2.0,
                DominioSexo.HEMBRA, null, 8.5, "Cafe", LocalDate.now(), null,
                DominioEstado.ACTIVO, new ArrayList<>());
        mascota2.setDueno(dueno1);
        dueno1.getMascotas().add(mascota2);
        mascotaRepository.save(mascota2);

        Mascota mascota3 = new Mascota(
                null, "Rex", DominioEspecie.PERRO, "Mestizo", 2.0,
                DominioSexo.MACHO, null, 8.5, "Cafe", LocalDate.now(), null,
                DominioEstado.ACTIVO, new ArrayList<>());
        mascota3.setDueno(dueno2);
        dueno2.getMascotas().add(mascota3);
        mascotaRepository.save(mascota3);
    }

    // CREATE 
    @Test
    public void StudentRepository_save_Dueno() {
        // Arrange
        Dueno dueno = new Dueno(
                null, "1920374", "Juan", "juan@example.com", "dueno123",
                "1032674780", new ArrayList<>());

        // Act
        Dueno savedDueno = duenoRepository.save(dueno);

        // Assert
        Assertions.assertThat(savedDueno).isNotNull();
    }

    // READ
    @Test
    public void StudentRepository_FindAll_NotEmptyList() {
        // Act
        List<Dueno> duenos = duenoRepository.findAll();

        // Assert: los 4 dueños creados en el init
        Assertions.assertThat(duenos).isNotNull();
        Assertions.assertThat(duenos.size()).isEqualTo(4);
        Assertions.assertThat(duenos.size()).isGreaterThan(0);
    }

    @Test
    public void StudentRepository_FindById_FindRightIndex() {
        // Arrange
        Dueno duenoExistente = duenoRepository.findByCedula("3001").get();
        Long index = duenoExistente.getId();

        // Act
        Optional<Dueno> dueno = duenoRepository.findById(index);

        // Assert
        Assertions.assertThat(dueno).isPresent();
        Assertions.assertThat(dueno.get().getCedula()).isEqualTo("3001");
    }

    @Test
    public void StudentRepository_FindById_FindWrongIndex() {
        // Arrange
        Long index = -1l;

        // Act
        Optional<Dueno> dueno = duenoRepository.findById(index);

        // Assert
        Assertions.assertThat(dueno).isEmpty();
    }

    @Test
    public void StudentRepository_findByCedula_Dueno() {
        // Arrange
        String cedula = "3001";

        // Act
        Optional<Dueno> dueno = duenoRepository.findByCedula(cedula);

        // Assert
        Assertions.assertThat(dueno).isPresent();
        Assertions.assertThat(dueno.get().getCorreo()).isEqualTo("dueno1@vetopia.com");
    }

    @Test
    public void StudentRepository_findByCedula_WrongCedula() {
        // Arrange
        String cedula = "9999999";

        // Act
        Optional<Dueno> dueno = duenoRepository.findByCedula(cedula);

        // Assert
        Assertions.assertThat(dueno).isEmpty();
    }

    // UPDATE
    @Test
    public void StudentRepository_updateByCedula_Dueno() {
        // Arrange
        String cedula = "3001";

        // Act
        Dueno dueno = duenoRepository.findByCedula(cedula).get();
        dueno.setNombre("Dueno 3001 Modificado");
        Dueno updatedDueno = duenoRepository.save(dueno);

        // Assert
        Assertions.assertThat(updatedDueno).isNotNull();
        Assertions.assertThat(updatedDueno.getNombre()).isEqualTo("Dueno 3001 Modificado");
    }

    // DELETE
    @Test
    public void StudentRepository_deleteById_EmptyDueno() {
        // Arrange
        Dueno duenoConDosMascotas = duenoRepository.findByCedula("3001").get();
        Long index = duenoConDosMascotas.getId();

        // Act
        duenoRepository.deleteById(index);

        // Assert
        Assertions.assertThat(duenoRepository.findById(index)).isEmpty();
        Assertions.assertThat(duenoRepository.findAll().size()).isEqualTo(3);
    }

}