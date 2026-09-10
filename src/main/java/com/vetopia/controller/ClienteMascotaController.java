package com.vetopia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.service.mascota.MascotaService;
import com.vetopia.service.tratamiento.TratamientoService;

@Controller
@RequestMapping("/cliente/mascotas")
public class ClienteMascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private TratamientoService tratamientoService;

    @GetMapping
    public String listarMascotas(@RequestParam Long duenoId, Model model) {
        model.addAttribute("mascotas", mascotaService.listarMascotasDeCliente(duenoId));
        model.addAttribute("duenoId", duenoId);
        return "cliente/listar-mascota";
    }

    @GetMapping("/detalle")
    public String verDetalle(@RequestParam(name = "id") Long id,
            @RequestParam Long duenoId, Model model) {
        try {
            model.addAttribute("duenoId", duenoId);
            model.addAttribute("mascota", mascotaService.obtenerMascotaDeCliente(id, duenoId));
            model.addAttribute("historial", tratamientoService.listarHistorialDeMascota(id));
        } catch (ParametroInvalidoException e) {
            model.addAttribute("mensajeError", e.getMessage());
        }
        return "cliente/detalle-mascota";
    }
}