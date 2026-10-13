import { Component, input, output } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';

@Component({
  selector: 'tr[app-fila-mascota]',
  templateUrl: './fila-mascota.component.html'
})
export class FilaMascotaComponent {
  mascota = input.required<Mascota>();
  verFicha = output<Mascota>();

  getEdad(mascota: Mascota): string {
    return `${mascota.edad} años`;
  }

  getDueno(mascota: Mascota): string {
    return mascota.dueno?.nombre || 'No registrado';
  }

  // Muestra el id como número de paciente de 4 dígitos.
  // padStart(4, '0') rellena con ceros a la izquierda: 7 -> "0007"; el ?? 0 cubre que id sea opcional.
  getNumeroPaciente(id?: number): string {
    return `PACIENTE N° ${String(id ?? 0).padStart(4, '0')}`;
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'https://via.placeholder.com/56?text=No+Image';
  }
}
