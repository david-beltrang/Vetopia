import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../../../service/dashboard.service';
import { DashboardTopTratamiento } from '../../../../models/dashboard-top-tratamiento.model';

@Component({
  selector: 'app-top-tratamientos',
  imports: [CommonModule],
  templateUrl: './top-tratamientos.component.html'
})
// Muestra los tratamientos más destacados
export class TopTratamientosComponent implements OnInit {
  private dashboardService = inject(DashboardService);

  tratamientos: DashboardTopTratamiento[] = [];
  cargando = true;
  error = false;

  // Obtiene el top de tratamientos para pintarlos en pantalla
  ngOnInit(): void {
    this.dashboardService.obtenerTopTratamientos().subscribe({
      next: (tratamientos) => {
        this.tratamientos = tratamientos;
        this.cargando = false;
      },
      error: () => {
        this.error = true;
        this.cargando = false;
      }
    });
  }
}
