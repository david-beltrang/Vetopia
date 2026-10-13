import { Component, output } from '@angular/core';

@Component({
  selector: 'app-mascota-no-encontrada',
  templateUrl: './mascota-no-encontrada.component.html',
  styles: [':host { display: block; }']
})
export class MascotaNoEncontradaComponent {
  volver = output<void>();
}
