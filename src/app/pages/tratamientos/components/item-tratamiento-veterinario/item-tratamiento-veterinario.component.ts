import { Component, input, output } from '@angular/core';
import { TratamientoDetalle } from '../../../../models/ficha-clinica.model';

export interface CambioEstadoTratamiento {
  tratamientoId: number;
  completado: boolean;
}

@Component({
  selector: 'app-item-tratamiento-veterinario',
  templateUrl: './item-tratamiento-veterinario.component.html'
})
export class ItemTratamientoVeterinarioComponent {
  item = input.required<TratamientoDetalle>();
  guardando = input(false);
  mensajeError = input('');

  // El componente solo avisa; la page es quien llama al service.
  estadoCambiado = output<CambioEstadoTratamiento>();

  esCompletado(): boolean {
    return this.item().tratamiento?.completado ?? false;
  }

  alternarEstado(): void {
    const id = this.item().tratamiento?.id;
    if (!id || this.guardando()) return;

    this.estadoCambiado.emit({
      tratamientoId: id,
      completado: !this.esCompletado()
    });
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
    return nombre ? 'Dr. ' + nombre : 'Desconocido';
  }

  nombreCliente(): string {
    return this.item().clienteNombre || 'No registrado';
  }

  nombrePaciente(): string {
    const nombre = this.item().tratamiento?.mascota?.nombre;
    return nombre || 'No registrado';
  }

  nombreDroga(): string {
    return this.item().drogaNombre || 'No registrado';
  }
}
