export type EstadoCita = 'CONFIRMADA' | 'CANCELADA' | 'COMPLETADA' | 'INASISTENCIA';

export interface MascotaCita {
  id: number;
  nombre: string;
  especie: string;
  raza: string;
}

export interface Cita {
  id: number;
  duenoId: number;
  duenoNombre: string;
  veterinarioId: number;
  veterinarioNombre: string;
  veterinarioEspecialidad: string;
  mascotas: MascotaCita[];
  fechaHoraInicio: string;
  fechaHoraFin: string;
  motivo: string;
  especialidad: string;
  estado: EstadoCita;
}

export interface ClasificacionMotivoCita {
  especialidad: string | null;
  requiereSeleccionManual: boolean;
  especialidadesDisponibles: string[];
  mensaje: string;
}

export interface HorarioDisponible {
  fechaHoraInicio: string;
  fechaHoraFin: string;
}

export interface CrearCita {
  duenoId: number;
  mascotaIds: number[];
  motivo: string;
  especialidad: string;
  fechaHoraInicio: string;
}
