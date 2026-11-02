import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KpiCardComponent } from '../kpi-card/kpi-card.component';
import { TratamientoService } from '../../../../service/tratamiento.service';
import { VeterinarioService } from '../../../../service/veterinario.service';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { DashboardService } from '../../../../service/dashboard.service';
import { DashboardKpis } from '../../../../models/dashboard-kpis.model';

@Component({
  selector: 'app-kpi-grid',
  imports: [CommonModule, KpiCardComponent],
  templateUrl: './kpi-grid.component.html'
})
// Carga y presenta los indicadores del dashboard
export class KpiGridComponent implements OnInit {
  totalTratamientosAplicados = 0;
  veterinariosActivos = 0;
  veterinariosInactivos = 0;
  totalMascotas = 0;
  mascotasEnTratamiento = 0;
  tratamientosUltimoMes = 0;
  ventasTotales = 0;
  gananciasTotales = 0;
  private tratamientoService = inject(TratamientoService);
  private veterinarioService = inject(VeterinarioService);
  private router = inject(Router);
  private dashboardService = inject(DashboardService);

  ngOnInit(): void {
    this.cargarDatos();
    this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe(() => {
      this.cargarDatos();
    });
  }

  // Carga los datos de los KPIs desde los servicios correspondientes
  cargarDatos(): void {
    this.dashboardService.obtenerKpis().subscribe({
      // Actualiza los valores de los KPIs al recibir la respuesta del servicio
      next: (kpis: DashboardKpis) => {
        this.totalMascotas = kpis.totalMascotas;
        this.mascotasEnTratamiento = kpis.mascotasEnTratamiento;
        this.tratamientosUltimoMes = kpis.tratamientosUltimoMes;
        this.ventasTotales = kpis.ventasTotales;
        this.gananciasTotales = kpis.gananciasTotales;
      }
    });
    // Obtiene el total de tratamientos aplicados y actualiza la propiedad correspondiente
    this.tratamientoService.obtenerTotalTratamientosAplicados().subscribe({
      next: (total) => {
        this.totalTratamientosAplicados = total;
      }
    });
    // Obtiene el total de veterinarios activos e inactivos y actualiza las propiedades correspondientes
    this.veterinarioService.contarVeterinariosActivos().subscribe({
      next: (total) => {
        this.veterinariosActivos = total;
      }
    });
    // Obtiene el total de veterinarios inactivos y actualiza la propiedad correspondiente
    this.veterinarioService.contarVeterinariosInactivos().subscribe({
      next: (total) => {
        this.veterinariosInactivos = total;
      }
    });
  }

  // Formatea un valor numérico como moneda en formato colombiano
  formatoMoneda(valor: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      maximumFractionDigits: 0
    }).format(valor);
  }
}
