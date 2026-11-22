package com.vetopia.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.vetopia.entities.Dueno;

import lombok.Data;

/**
 * DTO de salida para un dueño. Evita exponer la entidad completa (usuario y
 * mascotas) en las respuestas REST. La contraseña se mantiene porque el
 * formulario de edición del frontend la reutiliza.
 */
@Data
public class DuenoDTO {

    private Long id;
    private String cedula;
    private String nombre;
    private String correo;
    private String contrasena;
    private String celular;

    public static DuenoDTO from(Dueno dueno) {
        if (dueno == null) {
            return null;
        }

        DuenoDTO dto = new DuenoDTO();
        dto.setId(dueno.getId());
        dto.setCedula(dueno.getCedula());
        dto.setNombre(dueno.getNombre());
        dto.setCorreo(dueno.getCorreo());
        dto.setContrasena(dueno.getContrasena());
        dto.setCelular(dueno.getCelular());
        return dto;
    }

    public static List<DuenoDTO> from(List<Dueno> duenos) {
        if (duenos == null) {
            return null;
        }

        return duenos.stream()
                .map(DuenoDTO::from)
                .collect(Collectors.toList());
    }
}
