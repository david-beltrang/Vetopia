package com.vetopia.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Administrador;
import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.exception.RecursoNoEncontradoException;
import com.vetopia.repository.AdministradorRepository;

// Esta clase implementa la interfaz AdministradorService y define la lógica de negocio relacionada con administradores. Es el único punto al que los controladores acceden para obtener datos; a su vez, delega el acceso a datos en la capa repository
@Service
public class AdministradorServiceImpl implements AdministradorService {

    //Inyección de dependencias
    @Autowired
    private AdministradorRepository administradorRepository;

    // Devuelve todos los administradores registrados
    @Override
    public List<Administrador> listarAdministradores() {
        return administradorRepository.findAll();
    }

    // Busca un administrador por su identificador. Lanza una excepción si el identificador es nulo o si no se encuentra ningún administrador con ese identificador
    @Override
    public Administrador obtenerAdministradorPorId(Long id) {
        if (id == null) {
            throw new ParametroInvalidoException("No se especificó el identificador del administrador.");
        }
        Administrador administrador = administradorRepository.findById(id).orElse(null);
        if (administrador == null) {
            throw new RecursoNoEncontradoException("No se encontró ningún administrador con el identificador " + id + ".");
        }
        return administrador;
    }

    // Guarda un administrador en la base de datos. Lanza una excepción si el administrador es nulo
    @Override
    public void guardar(Administrador administrador) {
        if (administrador == null) {
            throw new ParametroInvalidoException("No se especificó el administrador a guardar.");
        }
        administradorRepository.save(administrador);
    }

    // Busca un administrador por su correo y contraseña. Lanza una excepción si el correo o la contraseña son nulos o vacíos
    @Override
    public Administrador obtenerPorCorreoYContrasena(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new ParametroInvalidoException("Debe indicar el correo y la contraseña.");
        }
        return administradorRepository.findByCorreoIgnoreCaseAndContrasena(correo, contrasena);
    }
}