package com.vetopia.config;

import java.time.LocalDate;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vetopia.entities.Administrador;
import com.vetopia.entities.Droga;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.entities.mascota.dominio_attr.DominioEspecie;
import com.vetopia.entities.mascota.dominio_attr.DominioSexo;
import com.vetopia.exception.RecursoNoEncontradoException;
import com.vetopia.repository.AdministradorRepository;
import com.vetopia.repository.DrogaRepository;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.TratamientoRepository;
import com.vetopia.repository.VeterinarioRepository;

// Script inicializador de datos de prueba. Puebla la BD al arrancar la
// aplicación con datos aleatorios con semilla y construye las relaciones entre
// objetos con las relaciones JPA y @Builder: primero las entidades
// independientes (duenos, veterinarios, administradores, drogas) y después las
// que se asocian (mascotas y tratamientos) eligiendo las entidades
// relacionadas con findById sobre un número al azar.
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private DrogaRepository drogaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    // Semilla fija: la distribución aleatoria es la misma en cada ejecución.
    private final Random random = new Random(42L);

    // Hace varias operaciones sobre la BD: una única transacción para que si
    // algo falla a la mitad, no queden datos parciales.
    @Override
    @Transactional
    public void run(String... args) {
        poblarDuenos();
        poblarVeterinarios();
        poblarAdministradores();
        poblarDrogas();
        poblarMascotas();
        poblarTratamientos();
    }

    private void poblarDuenos() {
        String[][] datos = {
                { "1020304050", "Ana Rodríguez", "ana@correo.com", "dueno123", "3001234567" },
                { "1020304051", "Pedro Gómez", "pedro@correo.com", "dueno123", "3007654321" },
                { "1020304052", "María López", "maria@correo.com", "dueno123", "3010001111" },
                { "1020304053", "Luis Martínez", "luis@correo.com", "dueno123", "3020002222" },
                { "1020304054", "Carmen Silva", "carmen@correo.com", "dueno123", "3030003333" } };
        for (String[] d : datos) {
            duenoRepository.save(Dueno.builder()
                    .cedula(d[0])
                    .nombre(d[1])
                    .correo(d[2])
                    .contrasena(d[3])
                    .celular(d[4])
                    .estado(estadoAleatorio())
                    .build());
        }
    }

    private void poblarVeterinarios() {
        String[] fotos = {
                "https://example.com/foto-carlos.jpg",
                "https://example.com/foto-laura.jpg" };
        String[][] datos = {
                { "1002003001", "Carlos Gutiérrez", "veterinario@vetopia.com", "Medicina General" },
                { "1002003002", "Laura Méndez", "laura.mendez@vetopia.com", "Cirugía" },
                { "1002003003", "Jorge Santana", "jorge.santana@vetopia.com", "Dermatología" },
                { "1002003004", "Sofía Ramírez", "sofia.ramirez@vetopia.com", "Odontología" },
                { "1002003005", "Andrés Peña", "andres.pena@vetopia.com", "Nutrición" } };
        for (String[] d : datos) {
            int indiceFoto = random.nextInt(fotos.length + 1);
            veterinarioRepository.save(Veterinario.builder()
                    .cedula(d[0])
                    .nombre(d[1])
                    .correo(d[2])
                    .especialidad(d[3])
                    .contrasena("vet123")
                    .numeroAtenciones(random.nextInt(30))
                    .estado(estadoAleatorio())
                    .urlFoto(indiceFoto == fotos.length ? null : fotos[indiceFoto])
                    .build());
        }
    }

    private void poblarAdministradores() {
        String[][] datos = {
                { "1000000001", "Dirección Vetopia", "admin@vetopia.com", "admin123" },
                { "1000000002", "Gerencia General", "gerencia@vetopia.com", "gerencia123" },
                { "1000000003", "Finanzas", "finanzas@vetopia.com", "finanzas123" } };
        for (String[] d : datos) {
            administradorRepository.save(Administrador.builder()
                    .cedula(d[0])
                    .nombre(d[1])
                    .correo(d[2])
                    .contrasena(d[3])
                    .build());
        }
    }

    private void poblarDrogas() {
        String[][] datos = {
                { "Amoxicilina", "8000", "15000", "100" },
                { "Ivermectina", "12000", "22000", "50" },
                { "Metronidazol", "6000", "11000", "75" },
                { "Carprofeno", "18000", "32000", "40" },
                { "Meloxicam", "15000", "28000", "60" } };
        for (String[] d : datos) {
            drogaRepository.save(Droga.builder()
                    .nombre(d[0])
                    .precioCompra(Double.parseDouble(d[1]))
                    .precioVenta(Double.parseDouble(d[2]))
                    .unidadesDisponibles(Integer.parseInt(d[3]))
                    .unidadesVendidas(random.nextInt(20))
                    .build());
        }
    }

    private void poblarMascotas() {
        long cantidadDuenos = duenoRepository.count();
        String[] nombres = { "Max", "Luna", "Rocky", "Bella", "Toby", "Nala", "Coco", "Milo", "Lola", "Simba" };
        String[] razas = { "Doberman", "Siamés", "Bulldog Francés", "Golden Retriever", "Beagle", "Persa",
                "Criollo", "Labrador", "Poodle", "Bengalí" };
        String[] colores = { "Negro", "Blanco", "Café", "Gris", "Atigrado", "Blanco y café", "Dorado", "Marrón" };
        String[] imagenes = {
                "https://imgs.search.brave.com/8AIQQXIiiwAhhRKhD87lE0EUhLc7Irg8eiwpgIx1x7U/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4u/cGl4YWJheS5jb20v/cGhvdG8vMjAxNi8w/My8yNy8xOC8xMi9s/dW5hLTEyODMzNTZf/NjQwLmpwZw",
                "https://imgs.search.brave.com/lyQdUI6O4tK7o734zlUOxuFLdw-1kUFmZssTDwlkam0/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5pc3RvY2twaG90/by5jb20vaWQvMTU0/MjEyMDY2L2VzL2Zv/dG8vZ2F0by1zaWFt/JUMzJUE5cy5qcGc_/cz02MTJ4NjEyJnc9/MCZrPTIwJmM9ZV9V/WDktSU5fZ3VPQWhw/bUxVYk40VGktWVBj/LU92c3hWQXJUOWpn/MWxVWT0",
                "https://imgs.search.brave.com/SpvOGsBrp_Lq8prflOD7fsZ5TRntmOqrUHvFvtSrWzg/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90aHVt/YnMuZHJlYW1zdGlt/ZS5jb20vYi9zZW50/YWRhLWdyaXMtZGVs/LXBlcnJpdG8tZG9n/by1mcmFuYyVDMyVB/OXMtYWlzbGFkYS0x/MDE3NTU1MzAuanBn" };

        int cantidadMascotas = random.nextInt(15);
        for (int i = 0; i < cantidadMascotas; i++) {
            int numeroDueno = random.nextInt(1, (int) cantidadDuenos + 1);
            Dueno dueno = duenoRepository.findById((long) numeroDueno)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Dueño no encontrado: " + numeroDueno));
            mascotaRepository.save(Mascota.builder()
                    .nombre(nombres[random.nextInt(nombres.length)])
                    .especie(DominioEspecie.values()[random.nextInt(DominioEspecie.values().length)])
                    .raza(razas[random.nextInt(razas.length)])
                    .edad(redondear(random.nextDouble() * 30))
                    .sexo(DominioSexo.values()[random.nextInt(DominioSexo.values().length)])
                    .imagen(imagenes[random.nextInt(imagenes.length)])
                    .pesoKg(redondear(random.nextDouble() * 50))
                    .color(colores[random.nextInt(colores.length)])
                    .fechaIngreso(LocalDate.now().minusDays(random.nextInt(400) + 1))
                    .dueno(dueno)
                    .estado(estadoAleatorio())
                    .build());
        }
    }

    private void poblarTratamientos() {
        long cantidadMascotas = mascotaRepository.count();
        long cantidadVeterinarios = veterinarioRepository.count();
        long cantidadDrogas = drogaRepository.count();
        String[] titulos = { "Vacunación anual", "Consulta general", "Control por dermatitis",
                "Extracción de cuerpo extraño", "Desparasitación", "Limpieza dental", "Curación de herida",
                "Control de peso" };
        String[] descripciones = { "Refuerzo múltiple y rabia. Próxima dosis en un año.",
                "Evaluación de rutina sin hallazgos.",
                "Aplicación de tratamiento. Pendiente revisión en 10 días.",
                "Procedimiento sin complicaciones. Recuperación satisfactoria.",
                "Tratamiento antiparasitario de amplio espectro.",
                "Limpieza y pulido dental realizado con éxito.",
                "Limpieza de la zona, drenaje y vendaje.",
                "Consulta con recomendaciones de alimentación y ejercicio." };

        int cantidadTratamientos = random.nextInt(20);
        for (int i = 0; i < cantidadTratamientos; i++) {
            int numeroMascota = random.nextInt(1, (int) cantidadMascotas + 1);
            Mascota mascota = mascotaRepository.findById((long) numeroMascota)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada: " + numeroMascota));
            int numeroVeterinario = random.nextInt(1, (int) cantidadVeterinarios + 1);
            Veterinario veterinario = veterinarioRepository.findById((long) numeroVeterinario)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Veterinario no encontrado: " + numeroVeterinario));
            int numeroDroga = random.nextInt(1, (int) cantidadDrogas + 1);
            Droga droga = drogaRepository.findById((long) numeroDroga)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Droga no encontrada: " + numeroDroga));
            tratamientoRepository.save(Tratamiento.builder()
                    .fecha(LocalDate.now().minusDays(random.nextInt(120) + 1))
                    .mascota(mascota)
                    .veterinario(veterinario)
                    .droga(droga)
                    .titulo(titulos[random.nextInt(titulos.length)])
                    .descripcion(descripciones[random.nextInt(descripciones.length)])
                    .completado(random.nextBoolean())
                    .build());
        }
    }

    private DominioEstado estadoAleatorio() {
        return DominioEstado.values()[random.nextInt(DominioEstado.values().length)];
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}