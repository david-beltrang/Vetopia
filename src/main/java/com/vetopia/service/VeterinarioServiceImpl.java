package com.vetopia.service;

import java.util.Collection;
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

    // Devuelve todos los veterinarios registrados
    @Override
    public List<Veterinario> listarVeterinarios() {
        Collection<Veterinario> veterinarios = veterinarioRepository.searchAll();
        return veterinarios == null ? List.of() : List.copyOf(veterinarios);
    }

    // Obtiene un veterinario validando previamente el identificador
    @Override
    public Veterinario obtenerVeterinarioPorId(Integer id) {
        // Validamos que el id no sea nulo antes de buscar el veterinario en el repositorio
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del veterinario.");
        }
        Veterinario veterinario = veterinarioRepository.searchById(id);
        if (veterinario == null) {
            throw new IllegalArgumentException("No se encontró ningún veterinario con el identificador " + id + ".");
        }
        return veterinario;
    }

    // Guarda (registra) un veterinario nuevo. El id lo asigna el repositorio
    @Override
    public void guardar(Veterinario veterinario) {
        if (veterinario == null) {
            throw new IllegalArgumentException("No se especificó el veterinario a guardar.");
        }
        veterinarioRepository.save(veterinario);
    }

    // Busca un veterinario por correo y contraseña (para login)
    @Override
    public Veterinario obtenerPorCorreoYContrasena(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el correo y la contraseña.");
        }
        return veterinarioRepository.searchByCorreoYContrasena(correo, contrasena);
    }
}
