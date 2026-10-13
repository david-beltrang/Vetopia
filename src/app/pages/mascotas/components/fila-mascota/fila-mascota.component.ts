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

  getNumeroPaciente(id: number): string {
    return `PACIENTE N° ${id.toString().padStart(4, '0')}`;
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'https://via.placeholder.com/56?text=No+Image';
  }
}
