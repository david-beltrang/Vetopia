package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Veterinario;

/**
 * SERVICIO - VeterinarioService
 * Define la lógica de negocio relacionada con veterinarios. Es el único
 * punto al que los controladores acceden para obtener datos; a su vez,
 * delega el acceso a datos en la capa repository.
 */
public interface VeterinarioService {

    /** Lista todos los veterinarios registrados. */
    List<Veterinario> listarVeterinarios();

    /** Obtiene un veterinario validando previamente el identificador. */
    Veterinario obtenerVeterinarioPorId(Integer id);

    /** Guarda (registra) un veterinario nuevo. El id lo asigna el repositorio. */
    void guardar(Veterinario veterinario);

    /** Busca un veterinario por correo y contraseña (para login). */
    Veterinario obtenerPorCorreoYContrasena(String correo, String contrasena);

    /** Actualiza los datos de un veterinario existente. */
    void actualizar(Veterinario veterinario);

    /** Elimina un veterinario por su id. */
    void eliminar(Integer id);
}
