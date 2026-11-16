import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { Cita, EstadoCita } from '../../../../models/cita.model';

export interface CambioEstadoCita {
  citaId: number;
  estado: EstadoCita;
}

@Component({
  selector: 'app-agenda-semanal',
  imports: [CommonModule],
  templateUrl: './agenda-semanal.component.html'
})
export class AgendaSemanalComponent {
  citas = input<Cita[]>([]);
  dias = input<Date[]>([]);
  modo = input<'veterinario' | 'administracion'>('veterinario');
  cambioEstado = output<CambioEstadoCita>();
  cancelar = output<number>();

  citasDelDia(dia: Date): Cita[] {
    const clave = this.fechaClave(dia);
    return this.citas()
      .filter(cita => cita.fechaHoraInicio.slice(0, 10) === clave)
      .sort((a, b) => a.fechaHoraInicio.localeCompare(b.fechaHoraInicio));
  }

  fechaClave(fecha: Date): string {
    const anio = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');
    return `${anio}-${mes}-${dia}`;
  }

  tituloDia(fecha: Date): string {
    return fecha.toLocaleDateString('es-CO', { weekday: 'short' }).replace('.', '');
  }

  fechaCorta(fecha: Date): string {
    return fecha.toLocaleDateString('es-CO', { day: 'numeric', month: 'short' }).replace('.', '');
  }

  hora(fecha: string): string {
    return fecha.slice(11, 16);
  }

  nombresMascotas(cita: Cita): string {
    return cita.mascotas.map(mascota => mascota.nombre).join(', ');
  }

  duracion(cita: Cita): number {
    return Math.round((new Date(cita.fechaHoraFin).getTime() - new Date(cita.fechaHoraInicio).getTime()) / 60000);
  }

  estadoVisible(estado: EstadoCita): string {
    const etiquetas: Record<EstadoCita, string> = {
      CONFIRMADA: 'Confirmada',
      CANCELADA: 'Cancelada',
      COMPLETADA: 'Completada',
      INASISTENCIA: 'Inasistencia'
    };
    return etiquetas[estado];
  }

  claseEstado(estado: EstadoCita): string {
    const clases: Record<EstadoCita, string> = {
      CONFIRMADA: 'bg-primary-container/20 text-on-primary-container',
      CANCELADA: 'bg-error-container/30 text-on-error-container',
      COMPLETADA: 'bg-emerald-50 text-emerald-800',
      INASISTENCIA: 'bg-amber-50 text-amber-900'
    };
    return clases[estado];
  }
}
