import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { API_URL } from '../config/api.config';
import { Veterinario } from '../models/veterinario.model';

@Injectable({
  providedIn: 'root',
})
export class VeterinarioService {
  private http = inject(HttpClient);
  private readonly url = `${API_URL}/veterinario`;

  listarVeterinarios(): Observable<Veterinario[]> {
    return this.http.get<Veterinario[]>(`${this.url}/all`);
  }

  obtenerVeterinarioPorId(id: number): Observable<Veterinario> {
    return this.http.get<Veterinario>(`${this.url}/${id}`);
  }

  // Devuelve el veterinario de la URL; si no hay id, no consulta la API.
  obtenerVeterinarioOpcional(id: number): Observable<Veterinario | undefined> {
    return id > 0 ? this.obtenerVeterinarioPorId(id) : of(undefined);
  }
}
