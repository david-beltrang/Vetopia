package com.vetopia.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Administrador {

    private Integer id;
    private String cedula;
    private String correo;
    private String contrasena;
    private String nombre;
}
