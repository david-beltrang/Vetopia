import { Component, input, output } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';
import { FilaMascotaComponent } from '../fila-mascota/fila-mascota.component';

@Component({
  selector: 'app-tabla-mascotas',
  imports: [FilaMascotaComponent],
  templateUrl: './tabla-mascotas.component.html'
})
export class TablaMascotasComponent {
  mascotas = input<Mascota[]>([]);
  textoContador = input('');
  verDetalle = output<Mascota>();
}
