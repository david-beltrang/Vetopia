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
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.service.DrogaService;
import com.vetopia.service.DuenoService;
import com.vetopia.service.MascotaService;
import com.vetopia.service.ResultadoTratamiento;
import com.vetopia.service.TratamientoService;
import com.vetopia.service.VeterinarioService;

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

    private Veterinario obtenerVeterinario(Integer veterinarioId) {
        if (veterinarioId == null) {
            return null;
        }
        return veterinarioService.obtenerVeterinarioPorId(veterinarioId);
    }

    @GetMapping
    public String listarMascotas(@RequestParam Integer veterinarioId, Model model) {
        model.addAttribute("mascotas", mascotaService.listarMascotasDeVeterinario(veterinarioId));
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        return "veterinario/mascotas-cargo";
    }

    @GetMapping("/registrar-cliente")
    public String registrarCliente(@RequestParam Integer veterinarioId, Model model) {
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        model.addAttribute("dueno", new Dueno());
        return "veterinario/registrar-cliente";
    }

    @PostMapping("/registrar-cliente")
    public String guardarCliente(@ModelAttribute Dueno dueno,
            @RequestParam Integer veterinarioId) {
        try {
            duenoService.registrarClienteActivo(dueno);
            return "redirect:/veterinario/mascotas/registrar-mascota?veterinarioId=" + veterinarioId
                    + "&duenoId=" + dueno.getId();
        } catch (IllegalArgumentException e) {
            return "redirect:/veterinario/mascotas/registrar-cliente?veterinarioId=" + veterinarioId
                    + "&error=No se pudo registrar el cliente.";
        }
    }

    @GetMapping("/registrar-mascota")
    public String registrarMascota(@RequestParam Integer veterinarioId,
            @RequestParam Long duenoId, Model model) {
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        model.addAttribute("duenoId", duenoId);
        model.addAttribute("mascota", new Mascota());
        return "veterinario/registrar-mascotas";
    }

    @PostMapping("/registrar-mascota")
    public String guardarMascota(@ModelAttribute Mascota mascota,
            @RequestParam Integer veterinarioId,
            @RequestParam(defaultValue = "finish") String action,
            @RequestParam Long duenoId,
            @RequestParam(required = false) MultipartFile foto, Model model) {
        mascotaService.registrarMascota(mascota, duenoId, foto);

        if ("newPet".equals(action)) {
            model.addAttribute("veterinarioId", veterinarioId);
            model.addAttribute("duenoId", duenoId);
            model.addAttribute("mascota", new Mascota());
            return "veterinario/registrar-mascotas";
        }
        return "redirect:/veterinario/mascotas?veterinarioId=" + veterinarioId;
    }

    @GetMapping("/tratamientos")
    public String listarTratamientos(@RequestParam Integer veterinarioId, Model model) {
        model.addAttribute("tratamientos",
                tratamientoService.listarTratamientosPorVeterinarioConDroga(veterinarioId));
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        return "veterinario/tratamientos";
    }

    @GetMapping("/asignar-tratamiento")
    public String asignarTratamiento(@RequestParam(name = "veterinarioId") Integer veterinarioId,
            Model model) {
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        model.addAttribute("mascotas", mascotaService.listarMascotas());
        model.addAttribute("drogas", drogaService.listarDrogas());
        model.addAttribute("tratamiento", new Tratamiento());
        return "veterinario/asignar-tratamiento";
    }

    @PostMapping("/asignar-tratamiento")
    public String aplicarTratamiento(@ModelAttribute Tratamiento tratamiento,
            @RequestParam Integer veterinarioId,
            @RequestParam Integer mascotaId,
            @RequestParam Long drogaId,
            Model model) {
        try {
            tratamiento.setMascota(mascotaService.obtenerMascotaPorId(mascotaId));
            tratamiento.setDroga(drogaService.obtenerDrogaPorId(drogaId));
            tratamiento.setVeterinario(obtenerVeterinario(veterinarioId));
            ResultadoTratamiento resultado = tratamientoService.asignarTratamiento(tratamiento, veterinarioId);
            model.addAttribute("mascota", resultado.getMascota());
            model.addAttribute("veterinarioId", veterinarioId);
            model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
            model.addAttribute("unidadesDescontadas", resultado.getUnidadesDescontadas());
            model.addAttribute("medicamentoNombre", resultado.getMedicamentoNombre());
            model.addAttribute("stockRestante", resultado.getStockRestante());
            model.addAttribute("mascotaId", resultado.getTratamiento().getMascota().getId());
            return "veterinario/tratamiento-confirmacion";
        } catch (IllegalArgumentException e) {
            return renderizarFormularioTratamiento(veterinarioId, model, e.getMessage());
        }
    }

    private String renderizarFormularioTratamiento(Integer veterinarioId, Model model, String mensaje) {
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        model.addAttribute("mascotas", mascotaService.listarMascotas());
        model.addAttribute("drogas", drogaService.listarDrogas());
        model.addAttribute("tratamiento", new Tratamiento());
        model.addAttribute("mensajeError", mensaje);
        return "veterinario/asignar-tratamiento";
    }

    @GetMapping("/ficha")
    public String verFichaClinica(@RequestParam Integer id,
            @RequestParam Integer veterinarioId, Model model) {
        try {
            Mascota mascota = mascotaService.obtenerMascotaPorId(id);
            Dueno propietario = mascota.getDueno();
            model.addAttribute("mascota", mascota);
            model.addAttribute("propietario", propietario);
            model.addAttribute("historial", tratamientoService.listarHistorialDeMascota(id));
            model.addAttribute("veterinarioId", veterinarioId);
            model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
            return "veterinario/ficha-clinica";
        } catch (IllegalArgumentException e) {
            return "redirect:/veterinario/mascotas?veterinarioId=" + veterinarioId;
        }
    }

    @PostMapping("/ficha/estado")
    public String cambiarEstado(@RequestParam Integer id,
            @RequestParam Integer veterinarioId,
            @RequestParam String estado) {
        try {
            mascotaService.cambiarEstado(id, estado);
            return "redirect:/veterinario/mascotas/ficha?veterinarioId="
                    + veterinarioId + "&id=" + id;
        } catch (IllegalArgumentException e) {
            return "redirect:/veterinario/mascotas/ficha?veterinarioId="
                    + veterinarioId + "&id=" + id;
        }
    }

    @PostMapping("/eliminar")
    public String eliminarMascota(@RequestParam Integer id,
            @RequestParam Integer veterinarioId) {
        try {
            mascotaService.eliminarMascota(id);
        } catch (IllegalArgumentException e) {
            return "redirect:/veterinario/mascotas?veterinarioId=" + veterinarioId
                    + "&error=No se pudo eliminar la mascota.";
        }
        return "redirect:/veterinario/mascotas?veterinarioId=" + veterinarioId;
    }

    @GetMapping("/editar")
    public String editarMascota(@RequestParam Integer veterinarioId,
            @RequestParam Integer id, Model model) {
        try {
            Mascota mascota = mascotaService.obtenerMascotaPorId(id);
            model.addAttribute("mascota", mascota);
            model.addAttribute("veterinarioId", veterinarioId);
            model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
            if (mascota.getDueno() != null) {
                model.addAttribute("duenoId", mascota.getDueno().getId());
            }
            return "veterinario/editar-mascota";
        } catch (IllegalArgumentException e) {
            return "redirect:/veterinario/mascotas?veterinarioId=" + veterinarioId;
        }
    }

    @PostMapping("/editar")
    public String guardarEdicionMascota(@ModelAttribute Mascota mascota,
            @RequestParam Integer veterinarioId,
            @RequestParam(required = false) MultipartFile foto) {
        mascotaService.actualizarMascota(mascota, foto);
        return "redirect:/veterinario/mascotas/ficha?veterinarioId=" + veterinarioId
                + "&id=" + mascota.getId();
    }
}