package com.vetopia.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.vetopia.entities.Administrador;

import lombok.Data;

/**
 * DTO de salida para un administrador. Evita exponer la entidad completa
 * (usuario) en las respuestas REST. La contraseña se mantiene por consistencia
 * con el DTO de dueño.
 */
@Data
public class AdministradorDTO {

    private Long id;
    private String cedula;
    private String nombre;
    private String correo;
    private String contrasena;

    public static AdministradorDTO from(Administrador administrador) {
        if (administrador == null) {
            return null;
        }

        AdministradorDTO dto = new AdministradorDTO();
        dto.setId(administrador.getId());
        dto.setCedula(administrador.getCedula());
        dto.setNombre(administrador.getNombre());
        dto.setCorreo(administrador.getCorreo());
        dto.setContrasena(administrador.getContrasena());
        return dto;
    }

    public static List<AdministradorDTO> from(List<Administrador> administradores) {
        if (administradores == null) {
            return null;
        }

        return administradores.stream()
                .map(AdministradorDTO::from)
                .collect(Collectors.toList());
    }
}
