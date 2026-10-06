import { Component, input, output } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';
import { Estado } from '../../../../models/enums/estado.enum';

@Component({
  selector: 'app-estado-paciente',
  templateUrl: './estado-paciente.component.html'
})
export class EstadoPacienteComponent {
  mascota = input<Mascota>();
  estadoCambiado = output<Mascota>();

  get esActivo(): boolean {
    return this.mascota()?.estado === 'Activo';
  }

  toggleEstado(): void {
    const mascota = this.mascota();
    if (!mascota) return;

    const nuevoEstado: Estado = this.esActivo ? 'Inactivo' : 'Activo';
    const actualizada: Mascota = Object.assign({}, mascota, { estado: nuevoEstado });
    this.estadoCambiado.emit(actualizada);
  }
}
