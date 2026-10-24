import { Component, output } from '@angular/core';

@Component({
  selector: 'app-acciones-error',
  templateUrl: './acciones-error.component.html'
})
export class AccionesErrorComponent {
  volverInicio = output<void>();
  regresar = output<void>();
}
