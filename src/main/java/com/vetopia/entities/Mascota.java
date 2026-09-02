package com.vetopia.entities;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mascota {

    
    private Integer id;
    private String nombre; 
    private String especie; 
    private String raza;
    private Double edad;
    private String sexo;
    private String imagen;
    private Double pesoKg;
    private String color;
    private LocalDate fechaIngreso;
    private Integer duenoId;
    private String estado;


    public boolean tieneImagen() {
        return imagen != null && !imagen.isBlank();
    }

    public String obtenerEdadDescripcion() {
        if (edad == null) {
            return "No registrada";
        }
        if (edad < 1) {
            long meses = Math.max(1, Math.round(edad * 12));
            return meses + (meses == 1 ? " mes" : " meses");
        }
        long anios = (long) Math.floor(edad);
        return anios + (anios == 1 ? " año" : " años");
    }
}
