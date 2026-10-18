import { Component, input } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';

@Component({
  selector: 'app-panel-foto-mascota',
  templateUrl: './panel-foto-mascota.component.html'
})
export class PanelFotoMascotaComponent {
  mascota = input.required<Mascota>();
}
