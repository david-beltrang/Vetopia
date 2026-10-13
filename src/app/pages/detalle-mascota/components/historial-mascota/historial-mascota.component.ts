import { Component, input } from '@angular/core';
import { TratamientoDetalle } from '../../../../models/ficha-clinica.model';
import { ItemHistorialComponent } from '../item-historial/item-historial.component';

@Component({
  selector: 'app-historial-mascota',
  imports: [ItemHistorialComponent],
  templateUrl: './historial-mascota.component.html'
})
export class HistorialMascotaComponent {
  historial = input<TratamientoDetalle[]>([]);
}
