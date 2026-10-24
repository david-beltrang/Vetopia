import { Component, input, output } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';

@Component({
  selector: 'app-fila-mascota-cliente',
  templateUrl: './fila-mascota-cliente.component.html'
})
export class FilaMascotaClienteComponent {
  mascota = input.required<Mascota>();
  verFicha = output<Mascota>();
}
