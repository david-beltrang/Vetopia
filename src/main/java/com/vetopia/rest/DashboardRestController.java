package com.vetopia.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetopia.dto.DashboardKpisDTO;
import com.vetopia.dto.DashboardMedicamentoDTO;
import com.vetopia.dto.DashboardTopTratamientoDTO;
import com.vetopia.service.dashboard.DashboardService;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardRestController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/kpis")
    public DashboardKpisDTO obtenerKpis() {
        return dashboardService.obtenerKpis();
    }

    @GetMapping("/top-tratamientos")
    public List<DashboardTopTratamientoDTO> obtenerTopTratamientos() {
        return dashboardService.obtenerTopTratamientos();
    }

    @GetMapping("/medicamentos-ultimo-mes")
    public List<DashboardMedicamentoDTO> obtenerMedicamentosUltimoMes() {
        return dashboardService.obtenerMedicamentosUltimoMes();
    }
}
