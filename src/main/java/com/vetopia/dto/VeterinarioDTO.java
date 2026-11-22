package com.vetopia.dto;

import com.vetopia.entities.Veterinario;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import lombok.Data;

/**
 * DTO de salida para un veterinario autenticado.
 * Expone solo los campos necesarios para el frontend, omitiendo la contraseña
 * y las relaciones JPA (tratamientos, user).
 */
@Data
public class VeterinarioDTO {

    private Long id;
    private String cedula;
    private String nombre;
    private String correo;
    private String especialidad;
    private Integer numeroAtenciones;
    private DominioEstado estado;
    private String urlFoto;

    public static VeterinarioDTO from(Veterinario veterinario) {
        if (veterinario == null) {
            return null;
        }

        VeterinarioDTO dto = new VeterinarioDTO();
        dto.setId(veterinario.getId());
        dto.setCedula(veterinario.getCedula());
        dto.setNombre(veterinario.getNombre());
        dto.setCorreo(veterinario.getCorreo());
        dto.setEspecialidad(veterinario.getEspecialidad());
        dto.setNumeroAtenciones(veterinario.getNumeroAtenciones());
        dto.setEstado(veterinario.getEstado());
        dto.setUrlFoto(veterinario.getUrlFoto());
        return dto;
    }
}
