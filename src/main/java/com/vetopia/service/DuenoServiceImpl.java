package com.vetopia.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
import com.vetopia.repository.DuenoRepository;

// Esta clase implementa la interfaz DuenoService y define la lógica de negocio relacionada con dueños. Es el único punto al que los controladores acceden para obtener datos; a su vez, delega el acceso a datos en la capa repository.
@Service
public class DuenoServiceImpl implements DuenoService {

    //Inyección de dependencias
    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private MascotaService mascotaService;

    // Devuelve todos los dueños registrados
    @Override
    public List<Dueno> listarDuenos() {
        Collection<Dueno> duenos = duenoRepository.searchAll();
        return duenos == null ? List.of() : List.copyOf(duenos);
    }

    // Obtiene un dueño validando previamente el identificador
    @Override
    public Dueno obtenerDuenoPorId(Integer id) {
        // Validamos que el id no sea nulo antes de buscar el dueño en el repositorio
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del dueño.");
        }
        Dueno dueno = duenoRepository.searchById(id);
        if (dueno == null) {
            throw new IllegalArgumentException("No se encontró ningún dueño con el identificador " + id + ".");
        }
        return dueno;
    }

    // Guarda (registra) un dueño nuevo. El id lo asigna el repositorio
    @Override
    public void guardar(Dueno dueno) {
        if (dueno == null) {
            throw new IllegalArgumentException("No se especificó el dueño a guardar.");
        }
        duenoRepository.save(dueno);
    }

    @Override
    public Dueno registrarClienteActivo(Dueno dueno) {
        if (dueno == null) {
            throw new IllegalArgumentException("No se especificó el cliente a registrar.");
        }
        dueno.setEstado("Activo");
        duenoRepository.save(dueno);
        return dueno;
    }

    // Elmina un dueño y en cascada sus mascotas y los tratamientos de estas.
    @Override
    public void eliminarCliente(Integer id) {
        obtenerDuenoPorId(id);
        for (Mascota mascota : mascotaService.listarMascotasDeCliente(id)) {
            mascotaService.eliminarMascota(mascota.getId());
        }
        duenoRepository.eliminar(id);
    }

    // Busca un dueño por correo y contraseña (para login)
    @Override
    public Dueno obtenerPorCorreoYContrasena(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el correo y la contraseña.");
        }
        return duenoRepository.searchByCorreoYContrasena(correo, contrasena);
    }
}
