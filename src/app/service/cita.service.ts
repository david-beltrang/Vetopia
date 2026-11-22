import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';
import { Cita, ClasificacionMotivoCita, CrearCita, EstadoCita, HorarioDisponible } from '../models/cita.model';

@Injectable({ providedIn: 'root' })
export class CitaService {
  private http = inject(HttpClient);
  private readonly url = `${API_URL}/cita`;

  listarEspecialidades(): Observable<string[]> {
    return this.http.get<string[]>(`${this.url}/especialidades`);
  }

  clasificarMotivo(motivo: string): Observable<ClasificacionMotivoCita> {
    return this.http.post<ClasificacionMotivoCita>(`${this.url}/clasificar-motivo`, { motivo });
  }

  listarDisponibilidad(fecha: string, cantidadMascotas: number, especialidad: string): Observable<HorarioDisponible[]> {
    const params = new HttpParams()
      .set('fecha', fecha)
      .set('cantidadMascotas', cantidadMascotas)
      .set('especialidad', especialidad);
    return this.http.get<HorarioDisponible[]>(`${this.url}/disponibilidad`, { params });
  }

  crearCita(solicitud: CrearCita): Observable<Cita> {
    return this.http.post<Cita>(this.url, solicitud);
  }

  listarDeDueno(duenoId: number): Observable<Cita[]> {
    return this.http.get<Cita[]>(`${this.url}/dueno/${duenoId}`);
  }

  listarDeVeterinario(veterinarioId: number, desde: string, hasta: string): Observable<Cita[]> {
    const params = new HttpParams().set('desde', desde).set('hasta', hasta);
    return this.http.get<Cita[]>(`${this.url}/veterinario/${veterinarioId}`, { params });
  }

  listarTodas(desde: string, hasta: string): Observable<Cita[]> {
    const params = new HttpParams().set('desde', desde).set('hasta', hasta);
    return this.http.get<Cita[]>(`${this.url}/administracion`, { params });
  }

  cancelarDeDueno(citaId: number, duenoId: number): Observable<Cita> {
    return this.http.put<Cita>(`${this.url}/${citaId}/cancelar`, null, {
      params: { duenoId }
    });
  }

  cancelarComoAdministrador(citaId: number): Observable<Cita> {
    return this.http.put<Cita>(`${this.url}/${citaId}/cancelar-administrador`, null);
  }

  actualizarEstado(citaId: number, veterinarioId: number, estado: EstadoCita): Observable<Cita> {
    return this.http.put<Cita>(`${this.url}/${citaId}/estado`, null, {
      params: { veterinarioId, estado }
    });
  }
}
