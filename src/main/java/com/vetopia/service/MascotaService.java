package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Mascota;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de mascotas en la aplicación.
public interface MascotaService {

    // Devuelve todas las mascotas registradas.
    List<Mascota> listarMascotas();

    // Obtiene una mascota validando previamente el identificador.
    Mascota obtenerMascotaPorId(Integer id);

    // Guarda una mascota nueva. El id lo asigna el repositorio.
    void guardar(Mascota mascota);

    // Cambia el estado de una mascota de "activo" a "inactivo".
    void cambiarEstado(Integer id, String estado);

    // Devuelve las mascotas asociadas a un veterinario: busca sus
    // tratamientos y de ahí obtiene las mascotas atendidas.
    List<Mascota> listarMascotasDeVeterinario(Integer veterinarioId);
}
