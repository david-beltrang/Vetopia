import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';
import { Mascota } from '../models/mascota.model';
import { Estado } from '../models/enums/estado.enum';

@Injectable({
  providedIn: 'root',
})
export class MascotaService {
  private http = inject(HttpClient);
  private readonly url = `${API_URL}/mascota`;

  listarMascotas(): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.url}/all`);
  }

  obtenerMascotaPorId(id: number): Observable<Mascota> {
    return this.http.get<Mascota>(`${this.url}/${id}`);
  }

  listarMascotasDeDueno(duenoId: number): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.url}/dueno/${duenoId}`);
  }

  buscarPorNombre(nombre: string): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.url}/buscar`, { params: { nombre } });
  }

  crearMascota(mascota: Mascota, duenoId: number): Observable<Mascota> {
    return this.http.post<Mascota>(this.url, mascota, { params: { duenoId } });
  }

  actualizarMascota(id: number, mascota: Mascota): Observable<Mascota> {
    return this.http.put<Mascota>(`${this.url}/${id}`, mascota);
  }

  cambiarEstado(id: number, estado: Estado): Observable<void> {
    return this.http.put<void>(`${this.url}/${id}/estado`, null, { params: { estado } });
  }

  eliminarMascota(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
