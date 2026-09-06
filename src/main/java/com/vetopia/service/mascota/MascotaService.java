package com.vetopia.service.mascota;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.vetopia.entities.mascota.Mascota;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de mascotas en la aplicación.
public interface MascotaService {

    // Devuelve todas las mascotas registradas.
    List<Mascota> listarMascotas();

    // Obtiene una mascota validando previamente el identificador.
    Mascota obtenerMascotaPorId(Long id);

    // Guarda una mascota nueva. El id lo asigna la base de datos.
    void guardar(Mascota mascota);

    // Registra una mascota nueva asignándole el dueño (relación JPA) y el estado
    // "Activo", y guardando la foto subida si la hay.
    Mascota registrarMascota(Mascota mascota, Long duenoId, MultipartFile foto);

    // Actualiza una mascota existente conservando dueño, estado e imagen
    // cuando esos campos no vienen en el formulario.
    Mascota actualizarMascota(Mascota mascota, MultipartFile foto);

    // Cambia el estado de una mascota de "activo" a "inactivo".
    void cambiarEstado(Long id, String estado);

    // Devuelve las mascotas asociadas a un dueño (cliente) concreto.
    List<Mascota> listarMascotasDeCliente(Long duenoId);

    // Devuelve las mascotas asociadas a un veterinario: busca sus
    // tratamientos y de ahí obtiene las mascotas atendidas (orquestación).
    List<Mascota> listarMascotasDeVeterinario(Long veterinarioId);

    // Obtiene la mascota validando que pertenezca al dueño indicado.
    Mascota obtenerMascotaDeCliente(Long mascotaId, Long duenoId);

}