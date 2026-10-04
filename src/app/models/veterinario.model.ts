import { Estado } from './enums/estado.enum';
import { Tratamiento } from './tratamiento.model';

export interface Veterinario {
  id: number;
  cedula: string;
  contrasena: string;
  correo: string;
  especialidad: string;
  numeroAtenciones: number;
  nombre: string;
  estado: Estado;
  urlFoto?: string;
  tratamientos: Tratamiento[];
}
