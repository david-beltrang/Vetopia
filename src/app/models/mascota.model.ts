import { Especie } from './enums/especie.enum';
import { Estado } from './enums/estado.enum';
import { Sexo } from './enums/sexo.enum';
import { Dueno } from './dueno.model';
import { Tratamiento } from './tratamiento.model';

export interface Mascota {
  id: number;
  nombre: string;
  especie: Especie;
  raza: string;
  edad: number;
  sexo: Sexo;
  imagen?: string;
  pesoKg: number;
  color: string;
  fechaIngreso: Date;
  dueno: Dueno;
  estado: Estado;
  tratamientos: Tratamiento[];
}
