package com.vetopia.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vetopia.entities.Administrador;
import com.vetopia.entities.Cita;
import com.vetopia.entities.CitaMascota;
import com.vetopia.entities.Droga;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.EstadoCita;
import com.vetopia.entities.Role;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.UserEntity;
import com.vetopia.entities.Veterinario;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.entities.mascota.dominio_attr.DominioEspecie;
import com.vetopia.entities.mascota.dominio_attr.DominioSexo;
import com.vetopia.exception.RecursoNoEncontradoException;
import com.vetopia.repository.AdministradorRepository;
import com.vetopia.repository.CitaMascotaRepository;
import com.vetopia.repository.CitaRepository;
import com.vetopia.repository.DrogaRepository;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.RoleRepository;
import com.vetopia.repository.TratamientoRepository;
import com.vetopia.repository.UserRepository;
import com.vetopia.repository.VeterinarioRepository;
import com.vetopia.service.tratamiento.TratamientoService;

// Script inicializador de datos para poblar la BD.
// En el perfil "test" se usa DataLoaderTest, que también carga datos de prueba.
// Sin esta exclusión ambos cargadores insertaban los mismos dueños y el contexto
// de Spring fallaba por violación de la restricción unique de la cédula.
@Component
@Profile("!test")
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
    private TratamientoService tratamientoService;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired 
    RoleRepository roleRepository;

    @Autowired 
    UserRepository userRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private CitaMascotaRepository citaMascotaRepository;

    // Fija la semilla para que los datos aleatorios generados sean consistentes entre ejecuciones
    private final Random random = new Random(42L);

    // Cantidad de datos de prueba: 50 clientes, 100 mascotas y 80 tratamientos.
    private static final int CANTIDAD_DUENOS = 50;
    private static final int CANTIDAD_MASCOTAS = 100;
    private static final int CANTIDAD_TRATAMIENTOS = 80;
    private String rutaDrogasExcel = "drogasVetopia.xlsx";

    // Opciones de nombres para generar clientes aleatorios con variedad
    private final String[] nombresPila = {
            "Ana", "Pedro", "María", "Luis", "Carmen", "Diego", "Valentina", "Andrés", "Laura", "Camilo",
            "Sofía", "Julián", "Paola", "Ricardo", "Daniela", "Felipe", "Natalia", "Óscar", "Lucía", "Gabriel",
            "Isabella", "Mateo", "Elena", "Samuel", "Antonia", "Héctor", "Carolina", "Esteban", "Mariana", "Tomás" };

    // Opciones de apellidos para generar clientes aleatorios con variedad
    private final String[] apellidos = {
            "Rodríguez", "Gómez", "López", "Martínez", "Silva", "García", "Rojas", "Hernández", "Pérez", "Torres",
            "Ramírez", "Vargas", "Castro", "Mora", "Jiménez", "Álvarez", "Ríos", "Quintero", "Salazar", "Navarro",
            "Ortiz", "Mendoza", "Cárdenas", "Pineda", "Aguilar", "Barrera", "Valencia", "Medina", "Guzmán", "Santos" };

    @Override
    @Transactional
    public void run(String... args) {
        roleRepository.save(new Role("ADMIN"));
        roleRepository.save(new Role("DUENO"));
        roleRepository.save(new Role("VETERINARIO"));

        poblarDuenos();
        poblarVeterinarios();
        poblarAdministradores();
        poblarDrogas();
        poblarMascotas();
        poblarTratamientos();
        poblarCitas();

        long totalTratamientosAplicados = tratamientoService.contarTratamientosAplicados();
        System.out.println("Total de tratamientos aplicados: " + totalTratamientosAplicados);
    }

    // Crea los clientes (dueños) de prueba. Lleva registro de las cédulas y
    // celulares ya usados para no violar las restricciones unique de la BD
    private void poblarDuenos() {
        Set<String> cedulasUsadas = new HashSet<>();
        Set<String> celularesUsados = new HashSet<>();
        // Crea los dueños con datos aleatorios y asigna cédula y celular únicos    
        for (int i = 0; i < CANTIDAD_DUENOS; i++) {
            String cedula = cedulaUnica(cedulasUsadas);

            // 1. Crear el objeto
            Dueno dueno = Dueno.builder()
                    .cedula(cedula)
                    .nombre(nombreAleatorio())
                    .correo("cliente" + cedula + "@correo.com")
                    .contrasena("dueno123")
                    .celular(celularUnico(celularesUsados))
                    .build();

            // 2. Guardarlo en la tabla User
            UserEntity userEntity = saveUser(dueno.getCorreo(), dueno.getContrasena(), "DUENO");

            // 3. Agregar al objeto del paso 1 el ID obtenido en el paso 2
            dueno.setUser(userEntity);

            // 4. Guardar el objeto en la tabla Dueno
            duenoRepository.save(dueno);
        }
    }

    // Genera un nombre completo aleatorio combinando dos apellidos y un nombre de pila
    private String nombreAleatorio() {
        return nombresPila[random.nextInt(nombresPila.length)] + " "
                + apellidos[random.nextInt(apellidos.length)] + " "
                + apellidos[random.nextInt(apellidos.length)];
    }

    // Genera una cédula de 10 dígitos que no se haya usado antes
    private String cedulaUnica(Set<String> usadas) {
        String cedula;
        do {
            cedula = Long.toString(random.nextLong(1_000_000_000L, 10_000_000_000L));
        } while (!usadas.add(cedula));
        return cedula;
    }

    // Genera un celular de 10 dígitos iniciado en 3 que no se haya usado antes
    private String celularUnico(Set<String> usados) {
        String celular;
        do {
            celular = "3" + Long.toString(random.nextLong(100_000_000L, 1_000_000_000L));
        } while (!usados.add(celular));
        return celular;
    }

    // Crea los veterinarios a partir de una lista
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
        // Crea los veterinarios con datos fijos y asigna una foto aleatoria (o ninguna)
        for (String[] d : datos) {
            int indiceFoto = random.nextInt(fotos.length + 1);

            // 1. Crear el objeto
            Veterinario veterinario = Veterinario.builder()
                    .cedula(d[0])
                    .nombre(d[1])
                    .correo(d[2])
                    .especialidad(d[3])
                    .contrasena("vet123")
                    .numeroAtenciones(random.nextInt(30))
                    // Se consume el mismo valor aleatorio para mantener estable
                    // la generación determinista de mascotas del DataLoader.
                    .estado(estadoActivoParaAgenda())
                    .urlFoto(indiceFoto == fotos.length ? null : fotos[indiceFoto])
                    .build();

            // 2. Guardarlo en la tabla User
            UserEntity userEntity = saveUser(veterinario.getCorreo(), veterinario.getContrasena(), "VETERINARIO");

            // 3. Agregar al objeto del paso 1 el ID obtenido en el paso 2
            veterinario.setUser(userEntity);

            // 4. Guardar el objeto en la tabla Veterinario
            veterinarioRepository.save(veterinario);
        }
    }

    // Crea los administradores del sistema con datos fijos
    private void poblarAdministradores() {
        String[][] datos = {
                { "1000000001", "Dirección Vetopia", "admin@vetopia.com", "admin123" },
                { "1000000002", "Gerencia General", "gerencia@vetopia.com", "gerencia123" },
                { "1000000003", "Finanzas", "finanzas@vetopia.com", "finanzas123" } };
        // Crea los administradores con datos fijos y asigna una foto aleatoria
        for (String[] d : datos) {
            // 1. Crear el objeto
            Administrador administrador = Administrador.builder()
                    .cedula(d[0])
                    .nombre(d[1])
                    .correo(d[2])
                    .contrasena(d[3])
                    .build();

            // 2. Guardarlo en la tabla User
            UserEntity userEntity = saveUser(administrador.getCorreo(), administrador.getContrasena(), "ADMIN");

            // 3. Agregar al objeto del paso 1 el ID obtenido en el paso 2
            administrador.setUser(userEntity);

            // 4. Guardar el objeto en la tabla Administrador
            administradorRepository.save(administrador);
        }
    }

    // Crea el inventario inicial de drogas con datos fijos. Las unidades vendidas
    private void poblarDrogas() {
        DataDrogasLoader drogasLoader = new DataDrogasLoader();
        drogasLoader.poblarMatrizDesdeExcel(rutaDrogasExcel);
        String[][] datos = drogasLoader.getMatrizDatos();
        
        for (String[] d : datos) {
            // Omite filas vacías o incompletas del Excel
            if (d[0] == null || d[0].isBlank() || d[1] == null || d[1].isBlank()) {
                continue;
            }
            drogaRepository.save(Droga.builder()
                    .nombre(d[0])
                    .precioCompra(Double.parseDouble(d[1].trim()))
                    .precioVenta(Double.parseDouble(d[2].trim()))
                    .unidadesDisponibles(Integer.parseInt(d[3].trim()))
                    .unidadesVendidas(Integer.parseInt(d[4].trim()))
                    .build());
        }
    }

    // Crea mascotas aleatorias asignando cada una a un dueño
    private void poblarMascotas() {
        long cantidadDuenos = duenoRepository.count();
        String[] nombres = { "Max", "Luna", "Rocky", "Bella", "Toby", "Nala", "Coco", "Milo", "Lola", "Simba",
                "Bruno", "Kira", "Thor", "Daisy", "Leo", "Maya", "Zeus", "Canela", "Oliver", "Mia",
                "Bobby", "Kiara", "Tommy", "Chispa", "Balu", "Frida", "Benji", "Pelusa", "Rex", "Sasha" };
        String[] razas = { "Doberman", "Siamés", "Bulldog Francés", "Golden Retriever", "Beagle", "Persa",
                "Criollo", "Labrador", "Poodle", "Bengalí" };
        String[] colores = { "Negro", "Blanco", "Café", "Gris", "Atigrado", "Blanco y café", "Dorado", "Marrón" };
        String[] imagenes = {
                "https://imgs.search.brave.com/8AIQQXIiiwAhhRKhD87lE0EUhLc7Irg8eiwpgIx1x7U/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4u/cGl4YWJheS5jb20v/cGhvdG8vMjAxNi8w/My8yNy8xOC8xMi9s/dW5hLTEyODMzNTZf/NjQwLmpwZw",
                "https://imgs.search.brave.com/lyQdUI6O4tK7o734zlUOxuFLdw-1kUFmZssTDwlkam0/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5pc3RvY2twaG90/by5jb20vaWQvMTU0/MjEyMDY2L2VzL2Zv/dG8vZ2F0by1zaWFt/JUMzJUE5cy5qcGc_/cz02MTJ4NjEyJnc9/MCZrPTIwJmM9ZV9V/WDktSU5fZ3VPQWhw/bUxVYk40VGktWVBj/LU92c3hWQXJUOWpn/MWxVWT0",
                "https://imgs.search.brave.com/SpvOGsBrp_Lq8prflOD7fsZ5TRntmOqrUHvFvtSrWzg/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90aHVt/YnMuZHJlYW1zdGlt/ZS5jb20vYi9zZW50/YWRhLWdyaXMtZGVs/LXBlcnJpdG8tZG9n/by1mcmFuYyVDMyVB/OXMtYWlzbGFkYS0x/MDE3NTU1MzAuanBn" };

        // Crea mascotas aleatorias asignando cada una a un dueño
        for (int i = 0; i < CANTIDAD_MASCOTAS; i++) {
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

    // Cada tratamiento se asocia con una mascota, un veterinario y una droga existentes
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

        // Genera una cantidad fija para que el dashboard siempre tenga datos suficientes.
        int cantidadTratamientos = CANTIDAD_TRATAMIENTOS;
        // Crea tratamientos aleatorios asignando cada uno a una mascota, un veterinario y una droga existentes
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

    // Crea citas demostrativas para que las agendas de cliente, veterinario y
    // administrador tengan datos desde el primer arranque.
    private void poblarCitas() {
        List<Veterinario> veterinarios = veterinarioRepository.findAll().stream()
                .filter(veterinario -> veterinario.getEstado() == DominioEstado.ACTIVO)
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .toList();
        List<Mascota> mascotas = mascotaRepository.findAll().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .toList();

        if (veterinarios.isEmpty() || mascotas.isEmpty()) {
            return;
        }

        String[] motivos = new String[veterinarios.size()];
        for (int i = 0; i < veterinarios.size(); i++) {
            motivos[i] = motivoDemoPorEspecialidad(veterinarios.get(i).getEspecialidad());
        }
        LocalDate primerDia = siguienteDiaDeAtencion(LocalDate.now(ZoneId.of("America/Bogota")).plusDays(1));
        LocalTime[] horas = {
                LocalTime.of(9, 0), LocalTime.of(10, 30), LocalTime.of(14, 0), LocalTime.of(15, 30)
        };

        int cantidadCitasDemo = Math.min(8, veterinarios.size() * 2);
        for (int i = 0; i < cantidadCitasDemo; i++) {
            Veterinario veterinario = veterinarios.get(i % veterinarios.size());
            Mascota mascota = mascotas.get((i * 7) % mascotas.size());
            LocalDate fecha = siguienteDiaDeAtencion(primerDia.plusDays(i / horas.length));
            LocalDateTime inicio = LocalDateTime.of(fecha, horas[i % horas.length]);

            Cita cita = citaRepository.save(Cita.builder()
                    .dueno(mascota.getDueno())
                    .veterinario(veterinario)
                    .fechaHoraInicio(inicio)
                    .fechaHoraFin(inicio.plusMinutes(30))
                    .motivo(motivos[i % motivos.length])
                    .especialidad(veterinario.getEspecialidad())
                    .estado(EstadoCita.CONFIRMADA)
                    .creadaEn(LocalDateTime.now(ZoneId.of("America/Bogota")))
                    .build());
            citaMascotaRepository.save(CitaMascota.builder().cita(cita).mascota(mascota).build());
        }
    }

    private String motivoDemoPorEspecialidad(String especialidad) {
        if (especialidad == null) return "Consulta preventiva y revisión general.";
        return switch (especialidad.toLowerCase()) {
            case "cirugía", "cirugia" -> "Valoración quirúrgica y revisión preoperatoria.";
            case "dermatología", "dermatologia" -> "Revisión de piel y seguimiento dermatológico.";
            case "odontología", "odontologia" -> "Valoración odontológica de rutina.";
            case "nutrición", "nutricion" -> "Control nutricional y recomendaciones de alimentación.";
            default -> "Consulta preventiva y revisión general.";
        };
    }

    // La primera versión opera con el horario común de lunes a sábado. Se
    // mantiene el método separado para reutilizar la regla al generar la demo.
    private LocalDate siguienteDiaDeAtencion(LocalDate fecha) {
        LocalDate resultado = fecha;
        while (resultado.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            resultado = resultado.plusDays(1);
        }
        return resultado;
    }

    // Todos los veterinarios demo están disponibles para que cada especialidad
    // tenga al menos un candidato de asignación. Se consume el aleatorio que
    // antes determinaba el estado para conservar el resto de la semilla.
    private DominioEstado estadoActivoParaAgenda() {
        estadoAleatorio();
        return DominioEstado.ACTIVO;
    }

    // Devuelve un estado aleatorio entre (activo - suspendido)
    private DominioEstado estadoAleatorio() {
        return DominioEstado.values()[random.nextInt(DominioEstado.values().length)];
    }

    // Redondea un valor a 2 decimales
    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    // Guarda las credenciales en la tabla User y retorna la entidad con el ID generado
    private UserEntity saveUser(String correo, String contrasena, String nombreRol) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(correo);
        userEntity.setPassword(passwordEncoder.encode(contrasena));
        Role role = roleRepository.findByName(nombreRol).get();
        userEntity.setRoles(List.of(role));
        return userRepository.save(userEntity);
    }
}
