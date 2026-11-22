package com.vetopia.service.veterinario;

import java.util.List;

import com.vetopia.entities.Veterinario;

// Esta interfaz define los métodos que el servicio de veterinarios debe implementar. 
// Los controladores interactúan con esta interfaz para realizar operaciones relacionadas con veterinarios,
// mientras que la implementación concreta se encarga de la lógica de negocio y delega el acceso a datos en la capa repository
public interface VeterinarioService {

    // Devuelve todos los veterinarios registrados
    List<Veterinario> listarVeterinarios();

    // Obtiene un veterinario validando previamente el identificador
    Veterinario obtenerVeterinarioPorId(Long id);

    // Guarda (registra) un veterinario nuevo. El id lo asigna la base de datos
    void guardar(Veterinario veterinario);

    // Registra un veterinario nuevo y devuelve el objeto resultante para poder
    // obtener su id tras el guardado
    Veterinario registrarVeterinario(Veterinario veterinario);

    // Busca un veterinario por correo y contraseña (para login)
    Veterinario obtenerPorCorreoYContrasena(String correo, String contrasena);

    Veterinario obtenerPorCorreo(String correo);

    // Elimina un veterinario por su id
    void eliminarVeterinario(Long id);

    long contarVeterinariosActivos();

    long contarVeterinariosInactivos();

    long contarAtenciones(Long veterinarioId);
}
