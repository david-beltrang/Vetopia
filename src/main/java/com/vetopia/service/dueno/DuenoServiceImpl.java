package com.vetopia.service.dueno;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vetopia.entities.Dueno;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
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
            throw new IllegalArgumentException("No se especificó el identificador del dueño.");
        }
        return duenoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró ningún dueño con el identificador " + id + "."));
    }

    // Guarda (registra) un dueño nuevo. El id lo asigna la base de datos.
    @Override
    public void guardar(Dueno dueno) {
        if (dueno == null) {
            throw new IllegalArgumentException("No se especificó el dueño a guardar.");
        }
        // El formulario de edición no envía el estado: se conserva el actual.
        if (dueno.getEstado() == null && dueno.getId() != null) {
            dueno.setEstado(obtenerDuenoPorId(dueno.getId()).getEstado());
        }
        duenoRepository.save(dueno);
    }

    @Override
    public Dueno registrarClienteActivo(Dueno dueno) {
        if (dueno == null) {
            throw new IllegalArgumentException("No se especificó el cliente a registrar.");
        }
        dueno.setEstado(DominioEstado.ACTIVO);
        duenoRepository.save(dueno);
        return dueno;
    }

    // Elimina un dueño y, en cascada por JPA (cascade=ALL + orphanRemoval), sus
    // mascotas y los tratamientos de estas. Usa @Transactional porque la cascada
    // implica varias operaciones sobre la base de datos.
    @Override
    @Transactional
    public void eliminarCliente(Long id) {
        obtenerDuenoPorId(id);
        duenoRepository.deleteById(id);
    }

    // Busca un dueño por correo y contraseña (para login)
    @Override
    public Dueno obtenerPorCorreoYContrasena(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el correo y la contraseña.");
        }
        return duenoRepository.findByCorreoIgnoreCaseAndContrasena(correo, contrasena).orElse(null);
    }
}