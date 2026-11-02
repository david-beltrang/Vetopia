import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HeaderAdminComponent } from '../../utils/header-admin/header-admin.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { DashboardEncabezadoComponent } from './components/dashboard-encabezado/dashboard-encabezado.component';
import { KpiGridComponent } from './components/kpi-grid/kpi-grid.component';
import { TratamientosPorMedicamentoComponent } from './components/tratamientos-por-medicamento/tratamientos-por-medicamento.component';
import { TopTratamientosComponent } from './components/top-tratamientos/top-tratamientos.component';

@Component({
  selector: 'app-dashboard',
  imports: [
    HeaderAdminComponent,
    FooterPortalComponent,
    DashboardEncabezadoComponent,
    KpiGridComponent,
    TratamientosPorMedicamentoComponent,
    TopTratamientosComponent
  ],
  templateUrl: './dashboard.component.html'
})
// Maneja la vista principal del administrador.
export class DashboardComponent implements OnInit {
  adminId = 0;
  private route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.adminId = Number(this.route.snapshot.queryParamMap.get('adminId')) || 0;
  }
}
