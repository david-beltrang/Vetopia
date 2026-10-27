package com.vetopia.service.veterinario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vetopia.entities.Veterinario;
import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.exception.RecursoNoEncontradoException;
import com.vetopia.repository.VeterinarioRepository;
import com.vetopia.repository.TratamientoRepository;

// Esta clase implementa la interfaz VeterinarioService y define la lógica de negocio relacionada con veterinarios. Es el único punto al que los controladores acceden para obtener datos; a su vez, delega el acceso a datos en la capa repository.
@Service
public class VeterinarioServiceImpl implements VeterinarioService {

    //Inyección de dependencias
    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    // Devuelve todos los veterinarios registrados
    @Override
    public List<Veterinario> listarVeterinarios() {
        return veterinarioRepository.findAll();
    }

    // Obtiene un veterinario validando previamente el identificador
    @Override
    public Veterinario obtenerVeterinarioPorId(Long id) {
        // Validamos que el id no sea nulo antes de buscar el veterinario en el repositorio
        if (id == null) {
            throw new ParametroInvalidoException("No se especificó el identificador del veterinario.");
        }
        Veterinario veterinario = veterinarioRepository.findById(id).orElse(null);
        if (veterinario == null) {
            throw new RecursoNoEncontradoException("No se encontró ningún veterinario con el identificador " + id + ".");
        }
        return veterinario;
    }

    // Guarda (registra) un veterinario nuevo. El id lo asigna la base de datos
    @Override
    public void guardar(Veterinario veterinario) {
        if (veterinario == null) {
            throw new ParametroInvalidoException("No se especificó el veterinario a guardar.");
        }
        veterinarioRepository.save(veterinario);
    }

    // Busca un veterinario por correo y contraseña
    @Override
    public Veterinario obtenerPorCorreoYContrasena(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new ParametroInvalidoException("Debe indicar el correo y la contraseña.");
        }
        return veterinarioRepository.findByCorreoIgnoreCaseAndContrasena(correo, contrasena);
    }

    // Elimina un veterinario y conserva sus tratamientos sin profesional asignado.
    @Override
    @Transactional
    public void eliminarVeterinario(Long id) {
        obtenerVeterinarioPorId(id);
        // Desvincula los tratamientos para que no sean eliminados en cascada.
        tratamientoRepository.findByVeterinarioId(id).forEach(tratamiento -> tratamiento.setVeterinario(null));
        tratamientoRepository.flush();
        veterinarioRepository.deleteById(id);
    }

    // Cuenta veterinarios activos
    @Override
    public long contarVeterinariosActivos() {
        return veterinarioRepository.countByEstado(com.vetopia.entities.mascota.dominio_attr.DominioEstado.ACTIVO);
    }

    // Cuenta veterinarios activos
    @Override
    public long contarVeterinariosInactivos() {
        return veterinarioRepository.countByEstado(com.vetopia.entities.mascota.dominio_attr.DominioEstado.INACTIVO);
    }

    // Cuenta la cantidad de atenciones por Veterinario
    @Override
    public long contarAtenciones(Long veterinarioId) {
        // Valida que el veterinario exista antes de contar sus tratamientos.
        obtenerVeterinarioPorId(veterinarioId);
        return tratamientoRepository.countByVeterinarioId(veterinarioId);
    }
}
