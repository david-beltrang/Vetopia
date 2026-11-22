import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';
import { Dueno } from '../models/dueno.model';
import { Mascota } from '../models/mascota.model';

@Injectable({
  providedIn: 'root',
})
export class DuenoService {
  private http = inject(HttpClient);
  private readonly url = `${API_URL}/dueno`;

  listarDuenos(): Observable<Dueno[]> {
    return this.http.get<Dueno[]>(`${this.url}/all`);
  }

  obtenerDuenoPorId(id: number): Observable<Dueno> {
    return this.http.get<Dueno>(`${this.url}/${id}`);
  }

  listarMascotasDeDueno(id: number): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.url}/${id}/mascotas`);
  }

  crearDueno(dueno: Dueno): Observable<Dueno> {
    return this.http.post<Dueno>(this.url, dueno);
  }

  actualizarDueno(id: number, dueno: Dueno): Observable<Dueno> {
    return this.http.put<Dueno>(`${this.url}/${id}`, dueno);
  }

  eliminarDueno(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  // Autentica al dueño con Spring Security y devuelve el JWT como string
  login(correo: string, contrasena: string): Observable<string> {
    return this.http.post(`${this.url}/login`, { correo, contrasena }, { responseType: 'text' });
  }
}
