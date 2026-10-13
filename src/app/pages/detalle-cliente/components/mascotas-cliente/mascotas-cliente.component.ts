import { Component, input, output } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';
import { FilaMascotaClienteComponent } from '../fila-mascota-cliente/fila-mascota-cliente.component';

@Component({
  selector: 'app-mascotas-cliente',
  imports: [FilaMascotaClienteComponent],
  templateUrl: './mascotas-cliente.component.html',
  styles: [':host { display: block; }']
})
export class MascotasClienteComponent {
  mascotas = input<Mascota[]>([]);
  verFicha = output<Mascota>();
}
