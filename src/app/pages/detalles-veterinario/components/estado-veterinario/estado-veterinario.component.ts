import { Component, input, output } from '@angular/core';
import { Veterinario } from '../../../../models/veterinario.model';
import { Estado } from '../../../../models/enums/estado.enum';

@Component({
  selector: 'app-estado-veterinario',
  templateUrl: './estado-veterinario.component.html'
})
// Permite consultar y cambiar el estado del veterinario
export class EstadoVeterinarioComponent {
  veterinario = input<Veterinario>();
  estadoCambiado = output<Veterinario>();

  // Devuelve true si el veterinario está activo, false si está inactivo
  get esActivo(): boolean {
    return this.veterinario()?.estado === 'Activo';
  }

  // Cambia el estado del veterinario y emite el evento con el nuevo estado
  toggleEstado(): void {
    const veterinario = this.veterinario();
    // Si no hay veterinario, no hacer nada
    if (!veterinario)
      return;

    const nuevoEstado: Estado = this.esActivo ? 'Inactivo' : 'Activo';
    const actualizada: Veterinario = Object.assign({}, veterinario, { estado: nuevoEstado });
    this.estadoCambiado.emit(actualizada);
  }
}
