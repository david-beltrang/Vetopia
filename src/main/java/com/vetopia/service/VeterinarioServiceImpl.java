package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Veterinario;
import com.vetopia.repository.VeterinarioRepository;

// Esta clase implementa la interfaz VeterinarioService y define la lógica de negocio relacionada con veterinarios. Es el único punto al que los controladores acceden para obtener datos; a su vez, delega el acceso a datos en la capa repository.
@Service
public class VeterinarioServiceImpl implements VeterinarioService {

    //Inyección de dependencias
    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Override
    public List<Veterinario> listarVeterinarios() {
        return List.copyOf(veterinarioRepository.searchAll());
    }

    @Override
    public Veterinario obtenerVeterinarioPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del veterinario.");
        }
        return veterinarioRepository.searchById(id);
    }

    @Override
    public void guardar(Veterinario veterinario) {
        veterinarioRepository.save(veterinario);
    }

    @Override
    public Veterinario obtenerPorCorreoYContrasena(String correo, String contrasena) {
        return veterinarioRepository.searchByCorreoYContrasena(correo, contrasena);
    }
}
