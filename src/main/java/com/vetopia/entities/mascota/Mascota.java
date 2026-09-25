package com.vetopia.entities.mascota;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.vetopia.entities.Dueno;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.entities.mascota.dominio_attr.DominioEspecie;
import com.vetopia.entities.mascota.dominio_attr.DominioSexo;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

//Anotacion para ORM
@Entity
@Table(name = "mascotas")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "especie", length = 50, nullable = false)
    private DominioEspecie especie;

    @Column(length = 50, nullable = false)
    private String raza;

    @Column(nullable = false)
    private Double edad;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", length = 10, nullable = false)
    private DominioSexo sexo;

    @Column(length = 500, nullable = true)
    private String imagen;

    @Column(nullable = false)
    private Double pesoKg;

    @Column(length = 50, nullable = false)
    private String color;

    @Column(nullable = false)
    private LocalDate fechaIngreso;

    // Relación n-1 con Dueno: esta es la parte dueña de la FK
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "dueno_id", nullable = false)
    private Dueno dueno;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private DominioEstado estado;

    // Relación 1-n con Tratamiento: mappedBy apunta a "mascota" en Tratamiento
    // dueño de la FK. Con cascade, al borrar la mascota se borran sus tratamientos
    @ToString.Exclude
    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL)
    private List<Tratamiento> tratamientos = new ArrayList<>();

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