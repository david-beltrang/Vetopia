import { Component, input, output } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';

@Component({
  selector: 'tr[app-fila-mascota]',
  templateUrl: './fila-mascota.component.html'
})
export class FilaMascotaComponent {
  mascota = input.required<Mascota>();
  verDetalle = output<Mascota>();

  numeroPaciente(): string {
    return 'Paciente N° ' + String(this.mascota().id).padStart(4, '0');
  }

  edad(): string {
    return `${this.mascota().edad} años`;
  }
}
