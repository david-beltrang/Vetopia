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


    // Método para verificar si la mascota tiene una imagen asociada
    public boolean tieneImagen() {
        return imagen != null && !imagen.isBlank();
    }

    // Método para obtener la descripción de la edad de la mascota
    public String obtenerEdadDescripcion() {
        // Si la edad es nula, se devuelve "No registrada"
        if (edad == null) {
            return "No registrada";
        }
        // Si la edad es menor a 1 año, se calcula en meses
        if (edad < 1) {
            // Se redondea la edad a meses y se asegura que sea al menos 1 mes
            long meses = Math.max(1, Math.round(edad * 12));
            return meses + (meses == 1 ? " mes" : " meses");
        }
        // Si la edad es 1 año o más, se calcula en años
        long anios = (long) Math.floor(edad);
        return anios + (anios == 1 ? " año" : " años");
    }
}
