package com.vetopia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.vetopia.service.MascotaService;

@Controller
@RequestMapping("/cliente/mascotas")
public class ClienteMascotaController {

    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    public String listarMascotas(@RequestParam Integer duenoId, Model model) {
        model.addAttribute("mascotas", mascotaService.listarMascotasDeCliente(duenoId));
        model.addAttribute("duenoId", duenoId);
        return "cliente/listar-mascota";
    }

    @GetMapping("/detalle")
    public String verDetalle(@RequestParam(name = "id") Integer id,
            @RequestParam Integer duenoId, Model model) {
        model.addAttribute("duenoId", duenoId);
        try {
            model.addAttribute("mascota", mascotaService.obtenerMascotaDeCliente(id, duenoId));
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
        }
        return "cliente/detalle-mascota";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String idNoNumerico(Model modelo) {
        modelo.addAttribute("mensajeError", "El identificador suministrado no es válido.");
        return "cliente/detalle-mascota";
    }
}