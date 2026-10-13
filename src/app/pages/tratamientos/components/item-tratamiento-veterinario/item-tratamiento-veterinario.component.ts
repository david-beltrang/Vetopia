import { Component, input } from '@angular/core';
import { TratamientoDetalle } from '../../../../models/ficha-clinica.model';

@Component({
  selector: 'app-item-tratamiento-veterinario',
  templateUrl: './item-tratamiento-veterinario.component.html'
})
export class ItemTratamientoVeterinarioComponent {
  item = input.required<TratamientoDetalle>();

  esCompletado(): boolean {
    return this.item().tratamiento?.completado ?? false;
  }

  // Se le añade la hora local para que la fecha no se corra un día.
  fechaFormateada(): string {
    const fecha = this.item().tratamiento?.fecha;
    if (!fecha) return '';
    return new Date(`${fecha}T00:00:00`).toLocaleDateString('es-ES', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
  }

  nombreMedico(): string {
    const nombre = this.item().tratamiento?.veterinario?.nombre;
    return nombre ? 'Dr. ' + nombre : 'No registrado';
  }

  nombreCliente(): string {
    const nombre = this.item().tratamiento?.mascota?.dueno?.nombre;
    return nombre || 'No registrado';
  }

  nombrePaciente(): string {
    const nombre = this.item().tratamiento?.mascota?.nombre;
    return nombre || 'No registrado';
  }

  nombreDroga(): string {
    return this.item().drogaNombre || 'No registrado';
  }
}
