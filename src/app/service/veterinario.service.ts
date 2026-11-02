import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { API_URL } from '../config/api.config';
import { Veterinario } from '../models/veterinario.model';
import { Estado } from '../models/enums/estado.enum';

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

  cambiarEstado(id: number, estado: Estado): Observable<Veterinario> {
    // Actualiza el estado sin reemplazar el resto de los datos del veterinario.
    return this.http.put<Veterinario>(`${this.url}/${id}/estado`, { estado });
  }

  contarVeterinariosActivos(): Observable<number> {
    return this.http.get<number>(`${this.url}/activos/count`);
  }

  contarVeterinariosInactivos(): Observable<number> {
    return this.http.get<number>(`${this.url}/inactivos/count`);
  }

  contarAtenciones(id: number): Observable<number> {
    return this.http.get<number>(`${this.url}/${id}/atenciones/count`);
  }

  crearVeterinario(veterinario: Veterinario): Observable<Veterinario> {
    return this.http.post<Veterinario>(`${this.url}`, veterinario);
  }

  eliminarVeterinario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
