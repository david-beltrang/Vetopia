import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

// Guard de autenticación que controla el acceso a las rutas protegidas
export const authGuard: CanActivateFn = () => {

  const auth = inject(AuthService);
  const router = inject(Router);

  // Verifica si el usuario tiene una sesión activa.
  if (auth.estaAutenticado()) {
    // Si el usuario está autenticado, permite el acceso a la ruta.
    return true;
  }

  // Si el usuario no está autenticado, se redirige a la página de login
  // haciendo que createUrlTree genere la navegación /login
  return router.createUrlTree(['/login']);
};