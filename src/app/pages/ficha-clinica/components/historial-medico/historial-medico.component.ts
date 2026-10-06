import { Component, input } from '@angular/core';
import { TratamientoDetalle } from '../../../../models/ficha-clinica.model';
import { ItemTratamientoComponent } from '../item-tratamiento/item-tratamiento.component';

@Component({
  selector: 'app-historial-medico',
  imports: [ItemTratamientoComponent],
  templateUrl: './historial-medico.component.html'
})
export class HistorialMedicoComponent {
  historial = input<TratamientoDetalle[]>([]);
}
