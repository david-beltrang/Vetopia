import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';
import { DashboardKpis } from '../models/dashboard-kpis.model';
import { DashboardMedicamento } from '../models/dashboard-medicamento.model';
import { DashboardTopTratamiento } from '../models/dashboard-top-tratamiento.model';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private http = inject(HttpClient);
  // Todos los indicadores del administrador usan esta ruta base.
  private readonly url = `${API_URL}/dashboard`;

  obtenerKpis(): Observable<DashboardKpis> {
    return this.http.get<DashboardKpis>(`${this.url}/kpis`);
  }

  // Obtiene el top de los tratamientos más usados
  obtenerTopTratamientos(): Observable<DashboardTopTratamiento[]> {
    return this.http.get<DashboardTopTratamiento[]>(`${this.url}/top-tratamientos`);
  }

  // Obtiene los medicamentos aplicados en el ultimo mes
  obtenerMedicamentosUltimoMes(): Observable<DashboardMedicamento[]> {
    return this.http.get<DashboardMedicamento[]>(`${this.url}/medicamentos-ultimo-mes`);
  }
}
