import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../../../service/dashboard.service';
import { DashboardMedicamento } from '../../../../models/dashboard-medicamento.model';

@Component({
  selector: 'app-tratamientos-por-medicamento',
  imports: [CommonModule],
  templateUrl: './tratamientos-por-medicamento.component.html'
})
export class TratamientosPorMedicamentoComponent implements OnInit {
  private dashboardService = inject(DashboardService);

  medicamentos: DashboardMedicamento[] = [];
  cargando = true;
  error = false;

  ngOnInit(): void {
    // Solicita los datos para alimentar la tabla del dashboard.
    this.dashboardService.obtenerMedicamentosUltimoMes().subscribe({
      next: (medicamentos) => {
        this.medicamentos = medicamentos;
        this.cargando = false;
      },
      error: () => {
        // Conserva el estado para mostrar un mensaje claro en la vista.
        this.error = true;
        this.cargando = false;
      }
    });
  }
}
