import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { authInterceptor } from './auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    // Registra HttpClient con el interceptor JWT y agrega el token en
    // cada petición autenticada
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};
