import { Mascota } from './mascota.model';
import { Droga } from './droga.model';
import { Tratamiento } from './tratamiento.model';

// Respuesta del endpoint POST /tratamiento/asignar.
export interface ResultadoTratamientoDTO {
  tratamiento: Tratamiento;
  mascota: Mascota;
  droga: Droga;
  unidadesDescontadas: number;
  stockRestante: number;
  medicamentoNombre: string;
}
