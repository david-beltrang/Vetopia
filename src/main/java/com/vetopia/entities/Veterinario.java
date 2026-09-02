package com.vetopia.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veterinario {

    
    private Integer id;
    private String cedula;
    private String contrasena;
    private String correo;
    private String especialidad;
    private Integer numeroAtenciones;
    private String nombre;
    private String estado;
    private String urlFoto;
}
