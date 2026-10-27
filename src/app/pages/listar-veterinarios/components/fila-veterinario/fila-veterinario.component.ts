import { Component, input, output } from '@angular/core';
import { Veterinario } from '../../../../models/veterinario.model';

@Component({
  selector: 'tr[app-fila-veterinario]',
  templateUrl: './fila-veterinario.component.html'
})
// Representa un veterinario dentro de la tabla.
export class FilaVeterinarioComponent {
  veterinario = input.required<Veterinario>();
  verDetalle = output<Veterinario>();

  iniciales(): string {
    const nombre = this.veterinario().nombre ?? '';
    return nombre ? nombre.slice(0, 2).toUpperCase() : 'VT';
  }

  // Verifica si el veterinario esta activo
  esActivo(): boolean {
    return this.veterinario().estado === 'Activo';
  }
}
