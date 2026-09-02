package com.vetopia.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.service.DrogaService;
import com.vetopia.service.DuenoService;
import com.vetopia.service.MascotaService;
import com.vetopia.service.TratamientoService;
import com.vetopia.service.VeterinarioService;


//CONTROLLER de portal del veterinario

@Controller
@RequestMapping("/veterinario/mascotas")
public class VeterinarioMascotaController {

    //Inyeccion de dependencias

    @Autowired
    private MascotaService mascotaService;


    @Autowired
    private DuenoService duenoService;


    @Autowired
    private TratamientoService tratamientoService;

  
    @Autowired
    private DrogaService drogaService;

    @Autowired
    private VeterinarioService veterinarioService;

    // Carga al veterinario logueado (el id llega por la URL) para mostrar
    // su nombre en el header; devuelve null si no hay id válido.
    private Veterinario obtenerVeterinario(Integer veterinarioId) {
        if (veterinarioId == null) {
            return null;
        }
        return veterinarioService.obtenerVeterinarioPorId(veterinarioId);
    }

    
     //Atiende GET /veterinario/mascotas?veterinarioId=N: listado de mascotas a cargo del veterinario.
    // veterinarioId es opcional: puede llegar por el login o faltar tras el registro.
    @GetMapping
    public String listarMascotas(@RequestParam(required = false) Integer veterinarioId, Model model) {
        model.addAttribute("mascotas", mascotaService.listarMascotasDeVeterinario(veterinarioId));
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        return "veterinario/mascotas-cargo";
    }

    /*
     Atiende GET /veterinario/mascotas/registrar-cliente: formulario del veterinario para registrar un nuevo cliente y su mascota.
     URL: http://localhost:8090/veterinario/mascotas/registrar-cliente
     */
    @GetMapping("/registrar-cliente")
    public String registrarCliente(@RequestParam Integer veterinarioId, Model model) {
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("dueno", new Dueno());
        return "veterinario/registrar-cliente";
    }

    /**
     Atiende POST /veterinario/mascotas/registrar-cliente: procesa el formulario de registro de un nuevo cliente.
     */
    @PostMapping("/registrar-cliente")
    //model attribute porque los datos van en el body
    public String guardarCliente(@ModelAttribute Dueno dueno,
            @RequestParam Integer veterinarioId) {
        dueno.setEstado("Activo");
        duenoService.guardar(dueno);
        return "redirect:/veterinario/mascotas/registrar-mascota?veterinarioId=" + veterinarioId + "&duenoId=" + dueno.getId();
    }

    /*
     Atiende GET /veterinario/mascotas/registrar-mascota: formulario
     del veterinario para registrar una mascota de un cliente recién registrado.
     
     URL: http://localhost:8090/veterinario/mascotas/registrar-mascota?veterinarioId=N&duenoId=M
     */
    @GetMapping("/registrar-mascota")
    public String registrarMascota(@RequestParam Integer veterinarioId, @RequestParam Integer duenoId, Model model) {
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("dueno", new Dueno());
        model.addAttribute("duenoId", duenoId);
        model.addAttribute("mascota", new Mascota());
        return "veterinario/registrar-mascotas";
    }

    // Atiende POST /veterinario/mascotas/registrar-mascota: procesa el formulario de registro de una nueva mascota.
    @PostMapping("/registrar-mascota")
    public String guardarMascota(@ModelAttribute Mascota mascota,
            @RequestParam Integer veterinarioId,
            @RequestParam(defaultValue = "finish") String action,
            @RequestParam Integer duenoId,
            //El multipart sirve para manejar la subida de fotos
            @RequestParam(required = false) MultipartFile foto, Model model) {

        
        mascota.setEstado("Activo");
        mascota.setDuenoId(duenoId);
        String imagen = guardarFoto(foto);
        if (imagen != null) {
            mascota.setImagen(imagen);
        }
        mascotaService.guardar(mascota);

        if ("newPet".equals(action)) {
            model.addAttribute("veterinarioId", veterinarioId);
            model.addAttribute("duenoId", duenoId);
            model.addAttribute("mascota", new Mascota());
            return "veterinario/registrar-mascotas";
        }
        // Al terminar el registro se redirige al listado de mascotas a cargo
        // (mascotas-cargo), propagando el veterinarioId obtenido del login.
        return "redirect:/veterinario/mascotas?veterinarioId=" + veterinarioId;
    }

    // Guarda la foto de la mascota en el directorio src/main/resources/static/images/mascotas
    public String guardarFoto(MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            return null;
        }
        try {
            String extension = "";
            String nombreOriginal = foto.getOriginalFilename();
            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf('.'));
            }
            String nombreArchivo = System.currentTimeMillis() + extension;
            Path directorio = Paths.get("src/main/resources/static/images/mascotas");
            Files.createDirectories(directorio);
            Files.copy(foto.getInputStream(), directorio.resolve(nombreArchivo),
                    StandardCopyOption.REPLACE_EXISTING);
            return "/images/mascotas/" + nombreArchivo;
        } catch (IOException excepcion) {
            return null;
        }
    }


    
    /*
     Atiende GET /veterinario/mascotas/asignar-tratamiento?veterinarioId=N: formulario del veterinario para asignar un tratamiento/medicamento a una mascota.
     El veterinarioId N viene del login (dinámico) y se propaga al formulario y al listado.
     */
    @GetMapping("/asignar-tratamiento")
    public String asignarTratamiento(@RequestParam(name = "veterinarioId", required = false) Integer veterinarioId,
            Model model) {
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        model.addAttribute("mascotas", mascotaService.listarMascotas());
        model.addAttribute("drogas", drogaService.listarDrogas());
        model.addAttribute("tratamiento", new Tratamiento());
        return "veterinario/asignar-tratamiento";
    }

    /**
     * Atiende POST /veterinario/mascotas/asignar-tratamiento: procesa la aplicacion de tratamiento
     */
    @PostMapping("/asignar-tratamiento")
    public String aplicarTratamiento(@ModelAttribute Tratamiento tratamiento, Model model) {
        if (tratamiento.getMascotaId() == null || tratamiento.getDrogaId() == null) {
            return asignarDeNuevo(model, "Debes seleccionar un paciente y un medicamento para asignar el tratamiento.");
        }

        Droga droga = drogaService.obtenerDrogaPorId(tratamiento.getDrogaId());
        if (droga == null) {
            return asignarDeNuevo(model, "El medicamento seleccionado no existe.");
        }

        int unidadesDisponibles = droga.getUnidadesDisponibles() == null ? 0 : droga.getUnidadesDisponibles();
        if (unidadesDisponibles <= 0) {
            return asignarDeNuevo(model, "El medicamento \"" + droga.getNombre() + "\" no tiene unidades disponibles.");
        }

        // El veterinario se asigna con el id que llega del formulario (campo
        // oculto veterinarioId), el cual proviene del login. Un tratamiento
        // sin veterinario no se registra: se devuelve al login.
        if (tratamiento.getVeterinarioId() == null) {
            return "redirect:/login";
        }
        tratamientoService.guardar(tratamiento);
        drogaService.descontarUnidades(tratamiento.getDrogaId(), 1);

        model.addAttribute("unidadesDescontadas", 1);
        model.addAttribute("medicamentoNombre", droga.getNombre());
        model.addAttribute("stockRestante", droga.getUnidadesDisponibles());
        model.addAttribute("mascotaId", tratamiento.getMascotaId());
        return "veterinario/tratamiento-confirmacion";
    }

    /**
     * Re-renderiza el formulario de asignación con el mensaje de error,
     * repoblando los datos necesarios de la vista.
     */
    private String asignarDeNuevo(Model model, String mensaje) {
        model.addAttribute("mascotas", mascotaService.listarMascotas());
        model.addAttribute("drogas", drogaService.listarDrogas());
        model.addAttribute("tratamiento", new Tratamiento());
        model.addAttribute("mensajeError", mensaje);
        return "veterinario/asignar-tratamiento";
    }

    /*
     Atiende GET /veterinario/mascotas/ficha?id=N: ficha clínica de una mascota para el portal del veterinario (historial médico y propietario).
     
     http://localhost:8090/veterinario/mascotas/ficha?id=2
     */
    @GetMapping("/ficha")
    public String verFichaClinica(@RequestParam Integer id, @RequestParam(required = false) Integer veterinarioId, Model model) {
        Mascota mascota = mascotaService.obtenerMascotaPorId(id);
        // La mascota guarda el id de su dueño (duenoId); se consulta ese
        // dueño en la BD simulada y se pasa a la vista como "propietario".
        Dueno propietario = null;
        if (mascota != null && mascota.getDuenoId() != null) {
            propietario = duenoService.obtenerDuenoPorId(mascota.getDuenoId());
        }
        // Historial médico: los tratamientos de esta mascota.
        List<HistorialItem> historial = new ArrayList<>();
        if (mascota != null) {
            for (Tratamiento tratamiento : tratamientoService.listarTratamientosPorMascota(mascota.getId())) {
                String nombreDroga = "Medicamento";
                if (tratamiento.getDrogaId() != null) {
                    Droga droga = drogaService.obtenerDrogaPorId(tratamiento.getDrogaId());
                    if (droga != null && droga.getNombre() != null) {
                        nombreDroga = droga.getNombre();
                    }
                }
                historial.add(new HistorialItem(tratamiento, nombreDroga));
            }
        }

        model.addAttribute("mascota", mascota);
        model.addAttribute("propietario", propietario);
        model.addAttribute("historial", historial);
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        return "veterinario/ficha-clinica";
    }

    /**
     * Atiende POST /veterinario/mascotas/ficha/estado: cambia el estado
     * (Activo/Inactivo) de una mascota. Es el endpoint del toggle
     * "Dar de alta / Reingresar" de la ficha clínica: el formulario envía
     * el id y el nuevo estado, y tras actualizarlo se vuelve a la ficha.
     */
    @PostMapping("/ficha/estado")
    public String cambiarEstado(@RequestParam Integer id,
            @RequestParam Integer veterinarioId,
            @RequestParam String estado) {
        mascotaService.cambiarEstado(id, estado);
        return "redirect:/veterinario/mascotas/ficha?veterinarioId="
                + (veterinarioId == null ? "" : veterinarioId) + "&id=" + id;
    }

   
}