package com.vetopia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.vetopia.entities.Dueno;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.entities.mascota.Mascota;
import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.exception.RecursoNoEncontradoException;
import com.vetopia.service.droga.DrogaService;
import com.vetopia.service.dueno.DuenoService;
import com.vetopia.service.mascota.MascotaService;
import com.vetopia.dto.ResultadoTratamientoDTO;
import com.vetopia.service.tratamiento.TratamientoService;
import com.vetopia.service.veterinario.VeterinarioService;

@Controller
@RequestMapping("/veterinario/mascotas")
public class VeterinarioMascotaController {

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

    private Veterinario obtenerVeterinario(Long veterinarioId) {
        if (veterinarioId == null) {
            return null;
        }
        return veterinarioService.obtenerVeterinarioPorId(veterinarioId);
    }

    // Atiende GET /veterinario/mascotas?veterinarioId=N: listado de mascotas
    @GetMapping
    public String listarMascotas(@RequestParam Long veterinarioId, Model model) {
        model.addAttribute("mascotas", mascotaService.listarMascotas());
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        return "veterinario/mascotas-cargo";
    }

    // Atiende GET /veterinario/mascotas/detalle?veterinarioId=N&id=M
    // Vista de detalle de una mascota
    @GetMapping("/registrar-cliente")
    public String registrarCliente(@RequestParam Long veterinarioId, Model model) {
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        model.addAttribute("dueno", new Dueno());
        return "veterinario/registrar-cliente";
    }

    // Atiende GET /veterinario/mascotas/registrar-mascota?veterinarioId=N&duenoId=M
    // Formulario para registrar una nueva mascota para un cliente existente
    @PostMapping("/registrar-cliente")
    public String guardarCliente(@ModelAttribute Dueno dueno,
            @RequestParam Long veterinarioId) {
        try {
            duenoService.registrarClienteActivo(dueno);
            return "redirect:/veterinario/mascotas/registrar-mascota?veterinarioId=" + veterinarioId
                    + "&duenoId=" + dueno.getId();
        } catch (ParametroInvalidoException | RecursoNoEncontradoException e) {
            return "redirect:/veterinario/mascotas/registrar-cliente?veterinarioId=" + veterinarioId
                    + "&error=No se pudo registrar el cliente.";
        }
    }

    // Atiende GET /veterinario/mascotas/registrar-mascota?veterinarioId=N&duenoId=M
    // Formulario para registrar una nueva mascota para un cliente existente
    @GetMapping("/registrar-mascota")
    public String registrarMascota(@RequestParam Long veterinarioId,
            @RequestParam Long duenoId, Model model) {
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        model.addAttribute("duenoId", duenoId);
        model.addAttribute("mascota", new Mascota());
        return "veterinario/registrar-mascotas";
    }

    // Atiende POST /veterinario/mascotas/registrar-mascota
    // Procesa el registro de una nueva mascota para un cliente existente
    @PostMapping("/registrar-mascota")
    public String guardarMascota(@ModelAttribute Mascota mascota,
            @RequestParam Long veterinarioId,
            @RequestParam(defaultValue = "finish") String action,
            @RequestParam Long duenoId,
            @RequestParam(required = false) MultipartFile foto, Model model) {
        try {
            mascotaService.registrarMascota(mascota, duenoId, foto);
        } catch (ParametroInvalidoException | RecursoNoEncontradoException e) {
            return "redirect:/veterinario/mascotas/registrar-mascota?veterinarioId=" + veterinarioId
                    + "&duenoId=" + duenoId + "&error=No se pudo registrar la mascota.";
        }

        if ("newPet".equals(action)) {
            model.addAttribute("veterinarioId", veterinarioId);
            model.addAttribute("duenoId", duenoId);
            model.addAttribute("mascota", new Mascota());
            return "veterinario/registrar-mascotas";
        }
        return "redirect:/veterinario/mascotas?veterinarioId=" + veterinarioId;
    }

    // Atiende GET /veterinario/mascotas/tratamientos?veterinarioId=N
    // Listado de tratamientos
    @GetMapping("/tratamientos")
    public String listarTratamientos(@RequestParam Long veterinarioId, Model model) {
        model.addAttribute("tratamientos",
                tratamientoService.listarTratamientosPorVeterinarioConDroga(veterinarioId));
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        return "veterinario/tratamientos";
    }

    // Atiende GET /veterinario/mascotas/asignar-tratamiento?veterinarioId=N&id=M
    // Formulario para asignar un tratamiento a una mascota
    @GetMapping("/asignar-tratamiento")
    public String asignarTratamiento(@RequestParam(name = "veterinarioId") Long veterinarioId,
            @RequestParam(name = "id") Long id,
            Model model) {
        try {
            Mascota mascota = mascotaService.obtenerMascotaPorId(id);
            model.addAttribute("veterinarioId", veterinarioId);
            model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
            model.addAttribute("mascota", mascota);
            model.addAttribute("dueno", mascota.getDueno());
            model.addAttribute("drogas", drogaService.listarDrogas());
            model.addAttribute("tratamiento", new Tratamiento());
            return "veterinario/asignar-tratamiento";
        } catch (ParametroInvalidoException | RecursoNoEncontradoException e) {
            return "redirect:/veterinario/mascotas?veterinarioId=" + veterinarioId;
        }
    }

    // Atiende POST /veterinario/mascotas/asignar-tratamiento
    // Procesa la asignación de un tratamiento a una mascota
    @PostMapping("/asignar-tratamiento")
    public String aplicarTratamiento(@ModelAttribute Tratamiento tratamiento,
            @RequestParam Long veterinarioId,
            @RequestParam(required = false) Long id, Model model) {
            try {
                Long mascotaId = id != null ? id
                        : (tratamiento.getMascota() != null ? tratamiento.getMascota().getId() : null);
                // Si no se proporciona el ID de la mascota, redirigir a la lista de mascotas 
                if (mascotaId == null) {
                    return "redirect:/veterinario/mascotas?veterinarioId=" + veterinarioId;
                }
                tratamiento.setMascota(mascotaService.obtenerMascotaPorId(mascotaId));
                tratamiento.setId(null);
                // Validar que se haya seleccionado un medicamento
                if (tratamiento.getDroga() == null || tratamiento.getDroga().getId() == null) {
                    return renderizarFormularioTratamiento(veterinarioId, mascotaId, model,
                            "Debes seleccionar un medicamento para asignar el tratamiento.");
                }
                tratamiento.setDroga(drogaService.obtenerDrogaPorId(tratamiento.getDroga().getId()));
                tratamiento.setVeterinario(obtenerVeterinario(veterinarioId));
                ResultadoTratamientoDTO resultado = tratamientoService.asignarTratamiento(tratamiento, veterinarioId);
                model.addAttribute("mascota", resultado.getMascota());
                model.addAttribute("veterinarioId", veterinarioId);
                model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
                model.addAttribute("unidadesDescontadas", resultado.getUnidadesDescontadas());
                model.addAttribute("medicamentoNombre", resultado.getMedicamentoNombre());
                model.addAttribute("stockRestante", resultado.getStockRestante());
                model.addAttribute("mascotaId", resultado.getTratamiento().getMascota().getId());
                return "veterinario/tratamiento-confirmacion";
        //Catch de excepciones desde el sevice
        } catch (ParametroInvalidoException | RecursoNoEncontradoException e) {
            Long mascotaId = id != null ? id
                    : (tratamiento.getMascota() != null ? tratamiento.getMascota().getId() : null);
            // Si no se proporciona el ID de la mascota, redirigir a la lista de mascotas
            if (mascotaId == null) {
                return "redirect:/veterinario/mascotas?veterinarioId=" + veterinarioId;
            }
            return renderizarFormularioTratamiento(veterinarioId, mascotaId, model, e.getMessage());
        }
    }

    // Renderiza el formulario de asignación de tratamiento con un mensaje de error
    private String renderizarFormularioTratamiento(Long veterinarioId, Long mascotaId, Model model, String mensaje) {
        try {
            Mascota mascota = mascotaService.obtenerMascotaPorId(mascotaId);
            model.addAttribute("mascota", mascota);
            model.addAttribute("dueno", mascota.getDueno());
        } catch (ParametroInvalidoException | RecursoNoEncontradoException e) {
            return "redirect:/veterinario/mascotas?veterinarioId=" + veterinarioId;
        }
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        model.addAttribute("drogas", drogaService.listarDrogas());
        model.addAttribute("tratamiento", new Tratamiento());
        model.addAttribute("mensajeError", mensaje);
        return "veterinario/asignar-tratamiento";
    }

    // Atiende GET /veterinario/mascotas/ficha?veterinarioId=N&id=M
    // Vista de ficha clínica de una mascota
    @GetMapping("/ficha")
    public String verFichaClinica(@RequestParam Long id,
            @RequestParam Long veterinarioId, Model model) {
        try {
            Mascota mascota = mascotaService.obtenerMascotaPorId(id);
            // Con la relación JPA, el propietario se obtiene navegando la relación.
            Dueno propietario = mascota.getDueno();
            model.addAttribute("mascota", mascota);
            model.addAttribute("propietario", propietario);
            model.addAttribute("historial", tratamientoService.listarHistorialDeMascota(id));
            model.addAttribute("veterinarioId", veterinarioId);
            model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
            return "veterinario/ficha-clinica";
        } catch (ParametroInvalidoException e) {
            return "redirect:/veterinario/mascotas?veterinarioId=" + veterinarioId;
        }
    }

    // Atiende POST /veterinario/mascotas/ficha/estado
    // Cambia el estado de una mascota (activo/inactivo)
    @PostMapping("/ficha/estado")
    public String cambiarEstado(@RequestParam Long id,
            @RequestParam Long veterinarioId,
            @RequestParam String estado) {
        try {
            mascotaService.cambiarEstado(id, estado);
            return "redirect:/veterinario/mascotas/ficha?veterinarioId="
                    + veterinarioId + "&id=" + id;
        } catch (ParametroInvalidoException | RecursoNoEncontradoException e) {
            return "redirect:/veterinario/mascotas/ficha?veterinarioId="
                    + veterinarioId + "&id=" + id;
        }
    }

    // Atiende GET /veterinario/mascotas/editar?veterinarioId=N&id=M
    // Formulario para editar una mascota existente
    @GetMapping("/editar")
    public String editarMascota(@RequestParam Long veterinarioId,
            @RequestParam Long id, Model model) {
        try {
            Mascota mascota = mascotaService.obtenerMascotaPorId(id);
            model.addAttribute("mascota", mascota);
            model.addAttribute("veterinarioId", veterinarioId);
            model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
            if (mascota.getDueno() != null) {
                model.addAttribute("duenoId", mascota.getDueno().getId());
            }
            return "veterinario/editar-mascota";
        } catch (ParametroInvalidoException e) {
            return "redirect:/veterinario/mascotas?veterinarioId=" + veterinarioId;
        }
    }

    // Atiende POST /veterinario/mascotas/editar
    // Procesa la edición de una mascota existente
    @PostMapping("/editar")
    public String guardarEdicionMascota(@ModelAttribute Mascota mascota,
            @RequestParam Long veterinarioId,
            @RequestParam(required = false) MultipartFile foto) {
        try {
            mascotaService.actualizarMascota(mascota, foto);
        } catch (ParametroInvalidoException | RecursoNoEncontradoException e) {
            return "redirect:/veterinario/mascotas/editar?veterinarioId=" + veterinarioId
                    + "&id=" + mascota.getId() + "&error=No se pudo actualizar la mascota.";
        }
        return "redirect:/veterinario/mascotas/ficha?veterinarioId=" + veterinarioId
                + "&id=" + mascota.getId();
    }
}