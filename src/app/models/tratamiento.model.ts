import { Mascota } from './mascota.model';
import { Veterinario } from './veterinario.model';
import { Droga } from './droga.model';

export interface Tratamiento {
  id: number;
  fecha: Date;
  mascota: Mascota;
  veterinario: Veterinario;
  droga: Droga;
  titulo: string;
  descripcion?: string;
  completado: boolean;
}
