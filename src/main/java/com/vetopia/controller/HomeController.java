package com.vetopia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vetopia.service.LoginService;
import com.vetopia.service.ResultadoLogin;

//Este controller gestiona las landing page y el login 
@Controller
public class HomeController {

    // Servicio de login
    @Autowired
    private LoginService loginService;

    //pantalla de landing page
    @GetMapping("/")
    public String index() {
        return "landing/index";
    }

    /*
     Atiende GET /login: pantalla de inicio de sesión del portal.
     http://localhost:8090/login
     */
    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    // Atiende POST /login: procesa el inicio de sesión del portal.
    @PostMapping("/login")
    public String iniciarSesion(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                Model model) {
        ResultadoLogin resultado = loginService.autenticar(username, password);
        if (resultado == null) {
            model.addAttribute("mensajeError", "Correo o contraseña incorrectos. Verifica tus credenciales.");
            return "login/login";
        }

        switch (resultado.getRol()) {
            case "VETERINARIO":
                return "redirect:/veterinario/mascotas?veterinarioId=" + resultado.getId();
            case "ADMINISTRADOR":
                //Este caso no está aún
                return "redirect:/admin";
            case "CLIENTE":
            default:
                return "redirect:/cliente/mascotas?duenoId=" + resultado.getId();
        }
    }
}
