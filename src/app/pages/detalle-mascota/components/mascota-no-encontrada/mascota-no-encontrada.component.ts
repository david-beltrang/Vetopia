import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-mascota-no-encontrada',
  templateUrl: './mascota-no-encontrada.component.html'
})
export class MascotaNoEncontradaComponent {
  mensaje = input('No encontramos ninguna mascota registrada.');
  verMascotas = output<void>();
}
