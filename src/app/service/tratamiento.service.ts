import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';
import { TratamientoDetalle } from '../models/ficha-clinica.model';
import { ResultadoTratamientoDTO } from '../models/resultado-tratamiento.model';

// Cuerpo mínimo que espera POST /tratamiento/asignar: solo los ids de la
// mascota y la droga (el veterinario va por la URL).
export interface TratamientoAsignacion {
  fecha: string;
  mascota: { id: number };
  droga: { id: number };
  titulo: string;
  descripcion?: string;
  completado?: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class TratamientoService {
  private http = inject(HttpClient);
  private readonly url = `${API_URL}/tratamiento`;

  listarHistorialDeMascota(mascotaId: number): Observable<TratamientoDetalle[]> {
    return this.http.get<TratamientoDetalle[]>(`${this.url}/mascota/${mascotaId}/historial`);
  }

  // Historial de todos sus pacientes, para la sección "Tratamientos".
  listarHistorialDeVeterinario(veterinarioId: number): Observable<TratamientoDetalle[]> {
    return this.http.get<TratamientoDetalle[]>(`${this.url}/veterinario/${veterinarioId}/historial`);
  }

  asignarTratamiento(tratamiento: TratamientoAsignacion, veterinarioId: number): Observable<ResultadoTratamientoDTO> {
    return this.http.post<ResultadoTratamientoDTO>(`${this.url}/asignar`, tratamiento, { params: { veterinarioId } });
  }
}
