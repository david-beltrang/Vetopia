import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    // Registra HttpClient en el inyector raíz para poder consumir la API REST
    // desde los servicios. En Angular standalone (bootstrapApplication) se usa
    // provideHttpClient() en lugar del antiguo HttpClientModule de los NgModule.
    // Sin esta línea, al inyectar HttpClient se produce un NullInjectorError.
    provideHttpClient()
  ]
};
