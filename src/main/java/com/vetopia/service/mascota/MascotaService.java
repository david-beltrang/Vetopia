package com.vetopia.service.mascota;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.vetopia.entities.mascota.Mascota;

// Esta interfaz define los métodos que deben implementarse para acceder y manipular los datos de mascotas en la aplicación.
public interface MascotaService {

    
    List<Mascota> listarMascotas();

    
    Mascota obtenerMascotaPorId(Long id);

    
    void guardar(Mascota mascota);

    
    Mascota registrarMascota(Mascota mascota, Long duenoId, MultipartFile foto);

    // Actualiza una mascota existente conservando dueño, estado e imagen
    // cuando esos campos no vienen en el formulario
    Mascota actualizarMascota(Mascota mascota, MultipartFile foto);

    // Cambia el estado de una mascota de "activo" a "inactivo"
    void cambiarEstado(Long id, String estado);

    // Devuelve las mascotas asociadas a un dueño (cliente) concreto
    List<Mascota> listarMascotasDeCliente(Long duenoId);

    // Devuelve las mascotas asociadas a un veterinario: busca sus
    // tratamientos y de ahí obtiene las mascotas atendidas
    List<Mascota> listarMascotasDeVeterinario(Long veterinarioId);

    // Obtiene la mascota validando que pertenezca al dueño indicado
    Mascota obtenerMascotaDeCliente(Long mascotaId, Long duenoId);

    // Busca mascotas por nombre
    List<Mascota> buscarPorNombre(String nombre);

    // Elimina una mascota por su id (sus tratamientos caen por cascade)
    void eliminarMascota(Long id);

}