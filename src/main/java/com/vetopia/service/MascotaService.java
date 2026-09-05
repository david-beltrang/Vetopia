package com.vetopia.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.vetopia.entities.Mascota;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de mascotas en la aplicación.
public interface MascotaService {

    // Devuelve todas las mascotas registradas.
    List<Mascota> listarMascotas();

    // Obtiene una mascota validando previamente el identificador.
    Mascota obtenerMascotaPorId(Integer id);

    // Guarda una mascota nueva. El id lo asigna el repositorio.
    void guardar(Mascota mascota);

    // Registra una mascota nueva asignándole el dueño y el estado "Activo",
    // y guardando la foto subida si la hay. El id lo asigna el repositorio.
    Mascota registrarMascota(Mascota mascota, Integer duenoId, MultipartFile foto);

    // Actualiza una mascota existente conservando dueño, estado e imagen
    // cuando esos campos no vienen en el formulario.
    Mascota actualizarMascota(Mascota mascota, MultipartFile foto);

    // Cambia el estado de una mascota de "activo" a "inactivo".
    void cambiarEstado(Integer id, String estado);

    // Elimina una mascota por su identificador.
    void eliminarMascota(Integer id);

    // Devuelve las mascotas asociadas a un dueño (cliente) concreto.
    List<Mascota> listarMascotasDeCliente(Integer duenoId);

    // Devuelve las mascotas asociadas a un veterinario: busca sus
    // tratamientos y de ahí obtiene las mascotas atendidas.
    List<Mascota> listarMascotasDeVeterinario(Integer veterinarioId);

    // Obtiene la mascota validando que pertenezca al dueño indicado.
    Mascota obtenerMascotaDeCliente(Integer mascotaId, Integer duenoId);

}