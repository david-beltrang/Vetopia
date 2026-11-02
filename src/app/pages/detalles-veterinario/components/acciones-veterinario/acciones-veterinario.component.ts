import { Component, output } from '@angular/core';

@Component({
  selector: 'app-acciones-veterinario',
  templateUrl: './acciones-veterinario.component.html',
  styles: [':host { display: block; width: 100%; }']
})
// Expone las acciones disponibles para el veterinario
export class AccionesVeterinarioComponent {
  eliminar = output<void>();
}
