import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MensajeErrorComponent } from './components/mensaje-error/mensaje-error.component';
import { AccionesErrorComponent } from './components/acciones-error/acciones-error.component';
import { FooterErrorComponent } from './components/footer-error/footer-error.component';

@Component({
  selector: 'app-error-page',
  imports: [
    MensajeErrorComponent,
    AccionesErrorComponent,
    FooterErrorComponent
  ],
  templateUrl: './error-page.component.html'
})
export class ErrorPageComponent {
  mensaje = 'No te preocupes, nada está perdido. Vuelve al inicio o intenta de nuevo.';
  private router = inject(Router);

  volverInicio(): void {
    this.router.navigate(['/']);
  }

  regresar(): void {
    // Si no hay historial al que volver, al menos sale de la página de error.
    if (window.history.length > 1) {
      window.history.back();
    } else {
      this.router.navigate(['/']);
    }
  }
}
