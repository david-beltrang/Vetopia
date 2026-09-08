package com.vetopia.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


// Mascota (paciente). Guarda los datos del animal y su relación con el dueño
// (cliente). Se encuentra en la ficha de "mis mascotas" del cliente y en el
// listado de pacientes del veterinario.
@Getter
@Setter
@ToString(exclude = {"tratamientos"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Mascota {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre; 
    private String especie; 
    private String raza;
    private Double edad;
    private String sexo;
    @Column(length = 1000)
    private String imagen;
    private Double pesoKg;
    private String color;
    private LocalDate fechaIngreso;
    private String estado;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Dueno dueno;

    @OneToMany(mappedBy = "mascota")
    private List<Tratamiento> tratamientos = new ArrayList<>();

    public Mascota(String nombre, String especie, String raza, Double edad, String sexo, String imagen, Double pesoKg, String color, LocalDate fechaIngreso, Dueno dueno, String estado) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.sexo = sexo;
        this.imagen = imagen;
        this.pesoKg = pesoKg;
        this.color = color;
        this.fechaIngreso = fechaIngreso;
        this.dueno = dueno;
        this.estado = estado;
    }

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