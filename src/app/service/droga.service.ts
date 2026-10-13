import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';
import { Droga } from '../models/droga.model';

@Injectable({
  providedIn: 'root',
})
export class DrogaService {
  private http = inject(HttpClient);
  private readonly url = `${API_URL}/droga`;

  listarDrogas(): Observable<Droga[]> {
    return this.http.get<Droga[]>(`${this.url}/all`);
  }

  obtenerDrogaPorId(id: number): Observable<Droga> {
    return this.http.get<Droga>(`${this.url}/${id}`);
  }
}
