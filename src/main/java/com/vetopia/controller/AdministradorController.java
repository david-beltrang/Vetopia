package com.vetopia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * CONTROLLER (Portal del Administrador)
 * Responsabilidad única: gestiona las peticiones del portal del
 * administrador. La vista es básicamente un panel de bienvenida pendiente
 * de desarrollo completo (dashboard de métricas, gestión de veterinarios, etc.).
 */
@Controller
public class AdministradorController {

    /**
     * Atiende GET /admin: panel principal del administrador.
     *
     * URL para visualizar: http://localhost:8080/admin
     * Vista: src/main/resources/templates/admin/dashboard.html
     */
    @GetMapping("/admin")
    public String dashboard() {
        return "admin/dashboard";
    }
}