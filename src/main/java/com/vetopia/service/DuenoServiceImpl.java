package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Dueno;
import com.vetopia.repository.DuenoRepository;

// Esta clase implementa la interfaz DuenoService y define la lógica de negocio relacionada con dueños. Es el único punto al que los controladores acceden para obtener datos; a su vez, delega el acceso a datos en la capa repository.
@Service
public class DuenoServiceImpl implements DuenoService {

    //Inyección de dependencias
    @Autowired
    private DuenoRepository duenoRepository;

    @Override
    public List<Dueno> listarDuenos() {
        return List.copyOf(duenoRepository.searchAll());
    }

    @Override
    public Dueno obtenerDuenoPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del dueño.");
        }
        return duenoRepository.searchById(id);
    }

    @Override
    public void guardar(Dueno dueno) {
        duenoRepository.save(dueno);
    }

    @Override
    public Dueno obtenerPorCorreoYContrasena(String correo, String contrasena) {
        return duenoRepository.searchByCorreoYContrasena(correo, contrasena);
    }
}
