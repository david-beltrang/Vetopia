import { Component, input, output } from '@angular/core';
import { Veterinario } from '../../../../models/veterinario.model';
import { FilaVeterinarioComponent } from '../fila-veterinario/fila-veterinario.component';

@Component({
  selector: 'app-tabla-veterinarios',
  imports: [FilaVeterinarioComponent],
  templateUrl: './tabla-veterinarios.component.html'
})
// Renderiza la tabla y sus estados vacíos
export class TablaVeterinariosComponent {
  // Recibe la lista y comunica las acciones seleccionadas en cada fila.
  veterinarios = input<Veterinario[]>([]);
  textoContador = input('');
  verDetalle = output<Veterinario>();
  nuevoVeterinario = output<void>();
}
