package com.vetopia.service.dashboard;

import com.vetopia.dto.DashboardKpisDTO;
import com.vetopia.dto.DashboardMedicamentoDTO;
import com.vetopia.dto.DashboardTopTratamientoDTO;

import java.util.List;

public interface DashboardService {

    // Reune los valores principales que se muestran en el dashboard
    DashboardKpisDTO obtenerKpis();

    List<DashboardTopTratamientoDTO> obtenerTopTratamientos();

    List<DashboardMedicamentoDTO> obtenerMedicamentosUltimoMes();
}
