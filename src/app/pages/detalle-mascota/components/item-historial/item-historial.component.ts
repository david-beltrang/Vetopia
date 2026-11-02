import { Component, input } from '@angular/core';
import { TratamientoDetalle } from '../../../../models/ficha-clinica.model';

@Component({
  selector: 'app-item-historial',
  templateUrl: './item-historial.component.html'
})
export class ItemHistorialComponent {
  item = input<TratamientoDetalle>();

  esCompletado(): boolean {
    return this.item()?.tratamiento?.completado ?? false;
  }

  fechaFormateada(): string {
    const fecha = this.item()?.tratamiento?.fecha;
    if (!fecha) return '';
    return new Date(`${fecha}T00:00:00`).toLocaleDateString('es-ES', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
  }

  nombreMedico(): string {
    const nombre = this.item()?.tratamiento?.veterinario?.nombre;
    return nombre ? 'Dr. ' + nombre : 'Desconocido';
  }
}
