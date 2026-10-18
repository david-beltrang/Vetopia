import { Component, input } from '@angular/core';

@Component({
  selector: 'app-mensaje-error',
  templateUrl: './mensaje-error.component.html'
})
export class MensajeErrorComponent {
  mensaje = input('No te preocupes, nada está perdido. Vuelve al inicio o intenta de nuevo.');
}
