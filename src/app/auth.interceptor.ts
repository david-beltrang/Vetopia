// Importa el interceptor funcional de Angular.
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { KEY_ID, KEY_ROL, KEY_TOKEN } from './service/auth.service';

// Interceptor que agrega el JWT a las peticiones HTTP y limpia la sesión
// cuando el backend responde 401 (token inválido, expirado o de otro arranque).
export const authInterceptor: HttpInterceptorFn = (req, next) => {

  // Obtiene el token almacenado en localStorage
  const token = localStorage.getItem(KEY_TOKEN);
  const router = inject(Router);

  // Clona la petición y agrega el encabezado Authorization si hay token
  const peticion = token
    ? req.clone({
        headers: req.headers.set('Authorization', 'Bearer ' + token)
      })
    : req;

  return next(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      const esLogin = req.url.includes('/login');
      // /auth/validate lo maneja la landing: limpia la sesión y se queda ahí.
      const esValidacion = req.url.endsWith('/auth/validate');

      if (error.status === 401 && !esLogin && !esValidacion) {
        localStorage.removeItem(KEY_TOKEN);
        localStorage.removeItem(KEY_ROL);
        localStorage.removeItem(KEY_ID);
        router.navigate(['/login']);
      }

      return throwError(() => error);
    })
  );
};
