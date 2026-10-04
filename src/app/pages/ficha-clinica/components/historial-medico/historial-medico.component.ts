import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TratamientoDetalle } from '../../../../models/ficha-clinica.model';
import { ItemTratamientoComponent } from '../item-tratamiento/item-tratamiento.component';

@Component({
  selector: 'app-historial-medico',
  imports: [CommonModule, ItemTratamientoComponent],
  templateUrl: './historial-medico.component.html'
})
export class HistorialMedicoComponent {
  historial = input<TratamientoDetalle[]>([]);
}
