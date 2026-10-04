import { Mascota } from './mascota.model';
import { Dueno } from './dueno.model';
import { Veterinario } from './veterinario.model';
import { Tratamiento } from './tratamiento.model';

export interface TratamientoDetalle {
  tratamiento: Tratamiento;
  drogaNombre: string;
}

export interface FichaClinicaDTO {
  mascota: Mascota;
  propietario: Dueno | null;
  historial: TratamientoDetalle[];
  veterinarioId: number;
  veterinario: Veterinario;
  mensajeError?: string;
}
