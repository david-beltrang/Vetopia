import { Component, input, output } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';
import { AvatarMascotaComponent } from '../avatar-mascota/avatar-mascota.component';

@Component({
  selector: 'app-modal-confirmacion',
  imports: [AvatarMascotaComponent],
  templateUrl: './modal-confirmacion.component.html'
})
export class ModalConfirmacionComponent {
  mascota = input<Mascota>();
  unidadesDescontadas = input(2);
  medicamentoNombre = input('Medicamento');
  stockRestante = input(0);

  volverFicha = output<void>();
  cerrar = output<void>();
}
