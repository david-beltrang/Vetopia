import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';
import { ResultadoLogin } from '../models/resultado-login.model';

export interface Credenciales {
  correo: string;
  contrasena: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private readonly url = `${API_URL}/auth`;

  login(credenciales: Credenciales): Observable<ResultadoLogin> {
    return this.http.post<ResultadoLogin>(`${this.url}/login`, credenciales);
  }
}
