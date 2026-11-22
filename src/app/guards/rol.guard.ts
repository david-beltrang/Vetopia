import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

// Recibe una lista de roles permitidos y devuelve un guard
// almacenado en el JWT (JSON Web Token)
export function rolGuard(rolesPermitidos: string[]): CanActivateFn {

  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const rol = auth.getRol();

    // Comprueba que el usuario tenga un rol asignado y que el rol sea permitido
    if (rol && rolesPermitidos.includes(rol)) {

      // Si ambas condiciones se cumplen, permite el acceso a la ruta
      return true;
    }

    // Si el usuario no tiene un rol válido o su rol no está permitido,
    // deniega el acceso y lo redirige a la página de error
    return router.createUrlTree(['/error']);
  };
}