import { Tratamiento } from './tratamiento.model';

export interface TratamientoDetalle {
  tratamiento: Tratamiento;
  drogaNombre: string;
  clienteNombre: string;
}
