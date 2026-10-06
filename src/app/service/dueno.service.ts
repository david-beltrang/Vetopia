import { inject, Injectable } from '@angular/core';
import { Dueno } from '../models/dueno.model';
import { MascotaService } from './mascota.service';

@Injectable({
  providedIn: 'root',
})
export class DuenoService {
  private duenos: Dueno[] = [
    { id: 1, cedula: '1023456789', nombre: 'Carlos Pérez', correo: 'carlos@email.com', contrasena: 'pass123', celular: '3001234567', mascotas: [] },
    { id: 2, cedula: '1098765432', nombre: 'María García', correo: 'maria@email.com', contrasena: 'pass123', celular: '3109876543', mascotas: [] },
    { id: 3, cedula: '1045678901', nombre: 'Juan Rodríguez', correo: 'juan@email.com', contrasena: 'pass123', celular: '3204567890', mascotas: [] },
    { id: 4, cedula: '1056789012', nombre: 'Ana Martínez', correo: 'ana@email.com', contrasena: 'pass123', celular: '3015678901', mascotas: [] },
    { id: 5, cedula: '1067890123', nombre: 'Luis Hernández', correo: 'luis@email.com', contrasena: 'pass123', celular: '3116789012', mascotas: [] },
  ];

  private nextId = 6;
  private mascotaService = inject(MascotaService);

  listarDuenos(): Dueno[] {
    return this.duenos.slice();
  }

  getDuenoById(id: number): Dueno | undefined {
    return this.duenos.find((d) => d.id === id);
  }

  createDueno(dueno: Dueno): Dueno {
    const nuevoDueno: Dueno = Object.assign({}, dueno, {
      id: this.nextId++,
      mascotas: [],
    });
    this.duenos.push(nuevoDueno);
    return nuevoDueno;
  }

  updateDueno(id: number, dueno: Dueno): Dueno | undefined {
    const index = this.duenos.findIndex((d) => d.id === id);
    if (index === -1) return undefined;

    this.duenos[index] = Object.assign({}, dueno, { id });
    return this.duenos[index];
  }

  eliminarCliente(id: number): boolean {
    this.mascotaService.removeMascotasDeDueno(id);
    const index = this.duenos.findIndex((d) => d.id === id);
    if (index === -1) return false;

    this.duenos.splice(index, 1);
    return true;
  }
}