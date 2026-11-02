import { Component, input } from '@angular/core';
import { Veterinario } from '../../../../models/veterinario.model';

@Component({
  selector: 'app-datos-veterinario',
  templateUrl: './datos-veterinario.component.html'
})
// Muestra los datos generales del veterinario
export class DatosVeterinarioComponent {
  veterinario = input<Veterinario>();
}
