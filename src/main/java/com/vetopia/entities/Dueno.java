package com.vetopia.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ENTIDAD - Dueno
 * Representa el dueño (cliente) de una o varias mascotas registradas en
 * Vetopia. No contiene lógica de acceso a datos ni de presentación, solo
 * el estado y las reglas propias de la entidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dueno {
    
    private Integer id;
    private String cedula;
    private String nombre;
    private String correo;
    private String contrasena;
    private String celular;
    private String estado;
}
