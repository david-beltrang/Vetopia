package com.vetopia.service.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.dto.DashboardKpisDTO;
import com.vetopia.dto.DashboardMedicamentoDTO;
import com.vetopia.dto.DashboardTopTratamientoDTO;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.TratamientoRepository;

import java.util.List;
import java.time.LocalDate;
import java.util.stream.IntStream;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Override
    public DashboardKpisDTO obtenerKpis() {
        return new DashboardKpisDTO(
                mascotaRepository.count(),
                mascotaRepository.countByEstado(DominioEstado.ACTIVO),
                tratamientoRepository.contarDesde(LocalDate.now().minusDays(30)),
                tratamientoRepository.sumVentasAplicadas(),
                tratamientoRepository.sumGananciasAplicadas());
    }

    @Override
    public List<DashboardTopTratamientoDTO> obtenerTopTratamientos() {
        // Calcula el porcentaje sobre el total antes de limitar el ranking.
        List<Object[]> agrupados = tratamientoRepository.contarTratamientosPorDroga();
        long totalTratamientos = agrupados.stream()
                .mapToLong(fila -> ((Number) fila[1]).longValue())
                .sum();

        return IntStream.range(0, Math.min(3, agrupados.size()))
                .mapToObj(indice -> {
                    Object[] fila = agrupados.get(indice);
                    long cantidad = ((Number) fila[1]).longValue();
                    double porcentaje = totalTratamientos == 0
                            ? 0
                            : Math.round(cantidad * 10000.0 / totalTratamientos) / 100.0;
                    return new DashboardTopTratamientoDTO((String) fila[0], cantidad, porcentaje);
                })
                .toList();
    }

    @Override
    public List<DashboardMedicamentoDTO> obtenerMedicamentosUltimoMes() {
        // El repositorio ya entrega los medicamentos ordenados por frecuencia.
        List<Object[]> agrupados = tratamientoRepository.contarMedicamentosDesde(LocalDate.now().minusDays(30));
        long totalTratamientos = agrupados.stream()
                .mapToLong(fila -> ((Number) fila[1]).longValue())
                .sum();

        return IntStream.range(0, Math.min(6, agrupados.size()))
                .mapToObj(indice -> {
                    Object[] fila = agrupados.get(indice);
                    long cantidad = ((Number) fila[1]).longValue();
                    double porcentaje = totalTratamientos == 0
                            ? 0
                            : Math.round(cantidad * 10000.0 / totalTratamientos) / 100.0;
                    return new DashboardMedicamentoDTO((String) fila[0], cantidad, porcentaje);
                })
                .toList();
    }
}
