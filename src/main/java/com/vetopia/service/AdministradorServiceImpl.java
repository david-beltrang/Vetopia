package com.vetopia.service;

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
        return List.copyOf(administradorRepository.searchAll());
    }

    @Override
    public Administrador obtenerAdministradorPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del administrador.");
        }
        return administradorRepository.searchById(id);
    }

    @Override
    public void guardar(Administrador administrador) {
        administradorRepository.save(administrador);
    }

    @Override
    public Administrador obtenerPorCorreoYContrasena(String correo, String contrasena) {
        return administradorRepository.searchByCorreoYContrasena(correo, contrasena);
    }
}
