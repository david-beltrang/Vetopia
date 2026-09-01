package com.vetopia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Veterinario;
import com.vetopia.service.DrogaService;
import com.vetopia.service.MascotaService;
import com.vetopia.service.TratamientoService;
import com.vetopia.service.VeterinarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private DrogaService drogaService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalMascotas", mascotaService.listarMascotas().size());
        model.addAttribute("totalVeterinarios", veterinarioService.listarVeterinarios().size());
        model.addAttribute("totalTratamientos", tratamientoService.listarTratamientos().size());
        model.addAttribute("totalDrogas", drogaService.listarDrogas().size());
        return "admin/dashboard";
    }

    @GetMapping("/veterinarios")
    public String listarVeterinarios(Model model) {
        model.addAttribute("veterinarios", veterinarioService.listarVeterinarios());
        return "admin/gestion-veterinarios";
    }

    @GetMapping("/veterinarios/registrar")
    public String registrarVeterinario(Model model) {
        model.addAttribute("veterinario", new Veterinario());
        return "admin/registrar-veterinario";
    }

    @PostMapping("/veterinarios/registrar")
    public String guardarVeterinario(@ModelAttribute Veterinario veterinario) {
        veterinario.setEstado("Activo");
        veterinarioService.guardar(veterinario);
        return "redirect:/admin/veterinarios";
    }

    @GetMapping("/veterinarios/editar")
    public String editarVeterinario(@RequestParam("id") Integer id, Model model) {
        Veterinario veterinario = veterinarioService.obtenerVeterinarioPorId(id);
        model.addAttribute("veterinario", veterinario);
        return "admin/editar-veterinario";
    }

    @PostMapping("/veterinarios/editar")
    public String guardarEdicionVeterinario(@ModelAttribute Veterinario veterinario) {
        veterinarioService.actualizar(veterinario);
        return "redirect:/admin/veterinarios";
    }

    @PostMapping("/veterinarios/estado")
    public String cambiarEstadoVeterinario(@RequestParam("id") Integer id, @RequestParam("estado") String estado) {
        Veterinario veterinario = veterinarioService.obtenerVeterinarioPorId(id);
        if (veterinario != null) {
            veterinario.setEstado(estado);
            veterinarioService.actualizar(veterinario);
        }
        return "redirect:/admin/veterinarios";
    }

    @PostMapping("/veterinarios/eliminar")
    public String eliminarVeterinario(@RequestParam("id") Integer id) {
        veterinarioService.eliminar(id);
        return "redirect:/admin/veterinarios";
    }

    @GetMapping("/drogas")
    public String listarDrogas(Model model) {
        model.addAttribute("drogas", drogaService.listarDrogas());
        return "admin/gestion-drogas";
    }

    @GetMapping("/drogas/registrar")
    public String registrarDroga(Model model) {
        model.addAttribute("droga", new Droga());
        return "admin/registrar-droga";
    }

    @PostMapping("/drogas/registrar")
    public String guardarDroga(@ModelAttribute Droga droga) {
        droga.setUnidadesVendidas(0);
        drogaService.guardar(droga);
        return "redirect:/admin/drogas";
    }

    @GetMapping("/drogas/editar")
    public String editarDroga(@RequestParam("id") Integer id, Model model) {
        Droga droga = drogaService.obtenerDrogaPorId(id);
        model.addAttribute("droga", droga);
        return "admin/editar-droga";
    }

    @PostMapping("/drogas/editar")
    public String guardarEdicionDroga(@ModelAttribute Droga droga) {
        drogaService.actualizar(droga);
        return "redirect:/admin/drogas";
    }

    @PostMapping("/drogas/eliminar")
    public String eliminarDroga(@RequestParam("id") Integer id) {
        drogaService.eliminar(id);
        return "redirect:/admin/drogas";
    }
}
