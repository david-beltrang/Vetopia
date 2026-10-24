import { Component, input, output } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';

@Component({
  selector: 'tr[app-fila-mascota]',
  templateUrl: './fila-mascota.component.html'
})
export class FilaMascotaComponent {
  mascota = input.required<Mascota>();
  duenosPorId = input<Map<number, string>>(new Map());
  verFicha = output<Mascota>();

  getEdad(mascota: Mascota): string {
    return `${mascota.edad} años`;
  }

  getDueno(mascota: Mascota): string {
    const duenoId = mascota.duenoId;
    if (duenoId == null) return 'No registrado';
    return this.duenosPorId().get(duenoId) || 'No registrado';
  }

  // Muestra el id como número de paciente de 4 dígitos.
  // padStart(4, '0') rellena con ceros a la izquierda: 7 -> "0007"; el ?? 0 cubre que id sea opcional.
  getNumeroPaciente(id?: number): string {
    return `PACIENTE N° ${String(id ?? 0).padStart(4, '0')}`;
  }

  // true cuando la mascota está activa; cualquier otro valor se muestra como inactiva
  esActiva(): boolean {
    return this.mascota().estado === 'Activo';
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'https://via.placeholder.com/56?text=No+Image';
  }
}
