package com.vetopia.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Administrador;
import com.vetopia.repository.AdministradorRepository;

// Esta clase implementa la interfaz AdministradorService y define la lógica de negocio relacionada con administradores. Es el único punto al que los controladores acceden para obtener datos; a su vez, delega el acceso a datos en la capa repository.
@Service
public class AdministradorServiceImpl implements AdministradorService {

    //Inyección de dependencias
    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    public List<Administrador> listarAdministradores() {
        Collection<Administrador> administradores = administradorRepository.searchAll();
        return administradores == null ? List.of() : List.copyOf(administradores);
    }

    @Override
    public Administrador obtenerAdministradorPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del administrador.");
        }
        Administrador administrador = administradorRepository.searchById(id);
        if (administrador == null) {
            throw new IllegalArgumentException("No se encontró ningún administrador con el identificador " + id + ".");
        }
        return administrador;
    }

    @Override
    public void guardar(Administrador administrador) {
        if (administrador == null) {
            throw new IllegalArgumentException("No se especificó el administrador a guardar.");
        }
        administradorRepository.save(administrador);
    }

    @Override
    public Administrador obtenerPorCorreoYContrasena(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el correo y la contraseña.");
        }
        return administradorRepository.searchByCorreoYContrasena(correo, contrasena);
    }
}
