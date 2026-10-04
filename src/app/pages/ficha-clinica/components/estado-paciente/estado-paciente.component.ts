import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Mascota } from '../../../../models/mascota.model';

@Component({
  selector: 'app-estado-paciente',
  imports: [CommonModule],
  templateUrl: './estado-paciente.component.html'
})
export class EstadoPacienteComponent {
  mascota = input<Mascota>();
  estadoCambiado = output<Mascota>();

  get esActivo(): boolean {
    return this.mascota()?.estado === 'Activo';
  }

  toggleEstado(): void {
    if (this.mascota()) {
      const nuevoEstado = this.esActivo ? 'Inactivo' : 'Activo';
      const mascotaActualizada: Mascota = Object.assign({}, this.mascota()!, {
        estado: nuevoEstado as 'Activo' | 'Inactivo',
      });
      this.estadoCambiado.emit(mascotaActualizada);
    }
  }
}
