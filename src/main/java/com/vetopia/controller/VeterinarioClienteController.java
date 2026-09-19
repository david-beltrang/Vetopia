package com.vetopia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vetopia.entities.Dueno;
import com.vetopia.entities.Veterinario;
import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.exception.RecursoNoEncontradoException;
import com.vetopia.service.dueno.DuenoService;
import com.vetopia.service.mascota.MascotaService;
import com.vetopia.service.veterinario.VeterinarioService;

//CONTROLLER de portal del veterinario para la gestión de clientes (dueños)

@Controller
@RequestMapping("/veterinario/clientes")
public class VeterinarioClienteController {

    //Inyeccion de dependencias

    @Autowired
    private DuenoService duenoService;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private VeterinarioService veterinarioService;

    // Carga al veterinario logueado (el id llega por la URL) para mostrar
    // su nombre en el header; devuelve null si no hay id válido.
    private Veterinario obtenerVeterinario(Long veterinarioId) {
        if (veterinarioId == null) {
            return null;
        }
        return veterinarioService.obtenerVeterinarioPorId(veterinarioId);
    }

    /*
     Atiende GET /veterinario/clientes?veterinarioId=N: listado de clientes
     registrados en Vetopia.
     URL: http://localhost:8090/veterinario/clientes?veterinarioId=1
     */
    @GetMapping
    public String listarClientes(@RequestParam Long veterinarioId, Model model) {
        model.addAttribute("clientes", duenoService.listarDuenos());
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        return "veterinario/listar-cliente";
    }

    /*
     Atiende GET /veterinario/clientes/detalle?veterinarioId=N&id=M: vista de
     detalle de un cliente (dueño).
     URL: http://localhost:8090/veterinario/clientes/detalle?veterinarioId=1&id=1
     */
    @GetMapping("/detalle")
    public String detalleCliente(@RequestParam Long veterinarioId,
            @RequestParam Long id, Model model) {
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        try {
            Dueno dueno = duenoService.obtenerDuenoPorId(id);
            model.addAttribute("dueno", dueno);
            model.addAttribute("mascotas", mascotaService.listarMascotasDeCliente(dueno.getId()));
        } catch (ParametroInvalidoException e) {
            model.addAttribute("mensajeError", e.getMessage());
        }
        return "veterinario/detalle-cliente";
    }

    /*
     Atiende GET /veterinario/clientes/editar?veterinarioId=N&id=M: formulario
     del veterinario para editar un cliente existente.
     URL: http://localhost:8090/veterinario/clientes/editar?veterinarioId=1&id=1
     */
    @GetMapping("/editar")
    public String editarCliente(@RequestParam Long veterinarioId,
            @RequestParam Long id, Model model) {
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("veterinario", obtenerVeterinario(veterinarioId));
        try {
            model.addAttribute("dueno", duenoService.obtenerDuenoPorId(id));
            return "veterinario/editar-cliente";
        } catch (ParametroInvalidoException e) {
            return "redirect:/veterinario/clientes?veterinarioId=" + veterinarioId;
        }
    }

    /*
     Atiende POST /veterinario/clientes/editar: procesa la edición de un
     cliente existente. Como el formulario trae el id, el repositorio
     actualiza el registro conservando el mismo identificador.
     */
    @PostMapping("/editar")
    public String guardarEdicionCliente(@ModelAttribute Dueno dueno,
            @RequestParam Long veterinarioId) {
        try {
            duenoService.guardar(dueno);
        } catch (ParametroInvalidoException | RecursoNoEncontradoException e) {
            return "redirect:/veterinario/clientes/editar?veterinarioId="
                    + veterinarioId + "&id=" + dueno.getId();
        }
        return "redirect:/veterinario/clientes/detalle?veterinarioId="
                + veterinarioId + "&id=" + dueno.getId();
    }

    /*
     Atiende POST /veterinario/clientes/eliminar: elimina el cliente indicado
     y, en cascada, sus mascotas y los tratamientos de estas; luego redirige
     al listado de clientes.
     */
    @PostMapping("/eliminar")
    public String eliminarCliente(@RequestParam Long id,
            @RequestParam Long veterinarioId) {
        try {
            duenoService.eliminarCliente(id);
        } catch (ParametroInvalidoException | RecursoNoEncontradoException e) {
            return "redirect:/veterinario/clientes?veterinarioId=" + veterinarioId;
        }
        return "redirect:/veterinario/clientes?veterinarioId=" + veterinarioId;
    }
}