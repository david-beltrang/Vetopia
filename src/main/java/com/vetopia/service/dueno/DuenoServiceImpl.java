package com.vetopia.service.dueno;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vetopia.entities.Dueno;
import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.exception.RecursoNoEncontradoException;
import com.vetopia.repository.DuenoRepository;

// Esta clase implementa la interfaz DuenoService y define la lógica de negocio relacionada con dueños. Es el único punto al que los controladores acceden para obtener datos; a su vez, delega el acceso a datos en la capa repository.
@Service
public class DuenoServiceImpl implements DuenoService {

    //Inyección de dependencias
    @Autowired
    private DuenoRepository duenoRepository;

    // Devuelve todos los dueños registrados
    @Override
    public List<Dueno> listarDuenos() {
        return duenoRepository.findAll();
    }

    // Obtiene un dueño validando previamente el identificador
    @Override
    public Dueno obtenerDuenoPorId(Long id) {
        // Validamos que el id no sea nulo antes de buscar el dueño en el repositorio
        if (id == null) {
            throw new ParametroInvalidoException("No se especificó el identificador del dueño.");
        }
        return duenoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró ningún dueño con el identificador " + id + "."));
    }

    // Busca un dueño por su cédula.
    @Override
    public Dueno obtenerPorCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            throw new ParametroInvalidoException("Debe indicar la cédula del dueño a buscar.");
        }
        return duenoRepository.findByCedula(cedula.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró ningún dueño con la cédula " + cedula + "."));
    }

    // Guarda un dueño nuevo. El id lo asigna la base de datos.
    @Override
    public void guardar(Dueno dueno) {
        if (dueno == null) {
            throw new ParametroInvalidoException("No se especificó el dueño a guardar.");
        }
        duenoRepository.save(dueno);
    }

    @Override
    public Dueno registrarClienteActivo(Dueno dueno) {
        if (dueno == null) {
            throw new ParametroInvalidoException("No se especificó el cliente a registrar.");
        }
        duenoRepository.save(dueno);
        return dueno;
    }

    // Elimina un dueño y, en cascada por sus
    // mascotas y los tratamientos de estas.
    @Override
    @Transactional
    public void eliminarCliente(Long id) {
        obtenerDuenoPorId(id);
        duenoRepository.deleteById(id);
    }

    // Busca un dueño por correo y contraseña para login
    @Override
    public Dueno obtenerPorCorreoYContrasena(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new ParametroInvalidoException("Debe indicar el correo y la contraseña.");
        }
        return duenoRepository.findByCorreoIgnoreCaseAndContrasena(correo, contrasena);
    }
}