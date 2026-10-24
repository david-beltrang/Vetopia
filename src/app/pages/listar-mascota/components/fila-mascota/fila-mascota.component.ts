import { Component, input, output } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';

@Component({
  selector: 'tr[app-fila-mascota]',
  templateUrl: './fila-mascota.component.html'
})
export class FilaMascotaComponent {
  mascota = input.required<Mascota>();
  verDetalle = output<Mascota>();

  // Número de paciente de 4 dígitos: padStart(4, '0') rellena con ceros a la izquierda (7 -> "0007").
  // El ?? 0 cubre que el id sea opcional en el modelo.
  numeroPaciente(): string {
    return 'Paciente N° ' + String(this.mascota().id ?? 0).padStart(4, '0');
  }

  edad(): string {
    return `${this.mascota().edad} años`;
  }

  // true cuando la mascota está activa; cualquier otro valor se muestra como inactiva
  esActiva(): boolean {
    return this.mascota().estado === 'Activo';
  }
}
