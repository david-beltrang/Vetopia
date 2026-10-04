import { Mascota } from './mascota.model';

export interface Dueno {
  id: number;
  cedula: string;
  nombre: string;
  correo: string;
  contrasena: string;
  celular: string;
  mascotas: Mascota[];
}
