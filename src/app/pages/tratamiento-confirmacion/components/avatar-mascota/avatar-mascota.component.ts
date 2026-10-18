import { Component, input } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';

@Component({
  selector: 'app-avatar-mascota',
  templateUrl: './avatar-mascota.component.html'
})
export class AvatarMascotaComponent {
  mascota = input<Mascota>();
}
