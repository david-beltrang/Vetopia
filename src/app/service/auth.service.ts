import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, catchError, map, of, switchMap, tap } from 'rxjs';
import { API_URL } from '../config/api.config';
import { ResultadoLogin } from '../models/resultado-login.model';

export interface Credenciales {
  correo: string;
  contrasena: string;
}

// Claves que se usan en localStorage (también las limpia el interceptor al recibir un 401)
export const KEY_TOKEN = 'token';
export const KEY_ROL = 'rol';
export const KEY_ID = 'userId';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private readonly urlAuth = `${API_URL}/auth`;
  private readonly urlDueno = `${API_URL}/dueno`;
  private readonly urlVeterinario = `${API_URL}/veterinario`;

  // 1. Identifica el rol vía /auth/login
  // 2. Obtiene el JWT del endpoint de Spring Security correspondiente
  // 3. Guarda token + rol + id en localStorage
  login(credenciales: Credenciales): Observable<ResultadoLogin> {
    // Envía las credenciales (usuario y contraseña) para validar el acceso
    return this.http
      .post<ResultadoLogin>(`${this.urlAuth}/login`, credenciales)
      .pipe(
        // switchMap recibe el resultado que devuelve el backend después del login
        // Permite ejecutar otra operación asíncrona utilizando ese resultado
        switchMap((resultado) =>
           // Solicita la generación del token JWT utilizando el rol recibido
          this.obtenerToken(resultado.rol, credenciales).pipe(
            // Permite ejecutar acciones secundarias sin modificar el valor emitido
            tap((token) => {
              localStorage.setItem(KEY_TOKEN, token);
              localStorage.setItem(KEY_ROL, resultado.rol);
              localStorage.setItem(KEY_ID, String(resultado.id));
            }),
            // Devuelve el resultado original del login después de obtener y almacenar el token JWT
            switchMap(() => [resultado])
          )
        )
      );
  }

  // Elimina todos los datos de sesión de localStorage
  logout(): void {
    localStorage.removeItem(KEY_TOKEN);
    localStorage.removeItem(KEY_ROL);
    localStorage.removeItem(KEY_ID);
  }

  // True si hay token en localStorage (el interceptor ya lo adjunta en cada request)
  estaAutenticado(): boolean {
    return !!localStorage.getItem(KEY_TOKEN);
  }

  // Comprueba con el backend que el JWT guardado siga siendo válido.
  // Da false si el token expiró, cambió la clave del backend o no hay token.
  validarSesion(): Observable<boolean> {
    return this.http.get(`${API_URL}/auth/validate`, { responseType: 'text' }).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  // Rol guardado al hacer login: 'VETERINARIO' | 'CLIENTE' | 'ADMINISTRADOR' | null
  getRol(): string | null {
    return localStorage.getItem(KEY_ROL);
  }

  // Id numérico del usuario autenticado
  getId(): number {
    return Number(localStorage.getItem(KEY_ID)) || 0;
  }

  // Llama al endpoint /login del rol correspondiente y devuelve el JWT como texto
  private obtenerToken(rol: string, credenciales: Credenciales): Observable<string> {
    const body = { correo: credenciales.correo, contrasena: credenciales.contrasena };

    if (rol === 'CLIENTE') {
      return this.http.post(`${this.urlDueno}/login`, body, { responseType: 'text' });
    }

    // VETERINARIO y ADMINISTRADOR comparten el mismo Spring Security UserDetails
    return this.http.post(`${this.urlVeterinario}/login`, body, { responseType: 'text' });
  }
}
