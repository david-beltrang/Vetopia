import { Injectable } from '@angular/core';
import { Mascota } from '../models/mascota.model';
import { Veterinario } from '../models/veterinario.model';
import { FichaClinicaDTO, TratamientoDetalle } from '../models/ficha-clinica.model';
import { Estado } from '../models/enums/estado.enum';

@Injectable({
  providedIn: 'root',
})
export class MascotaService {
  private mascotas: Mascota[] = [
    {
      id: 1,
      nombre: 'Max',
      especie: 'Perro',
      raza: 'Golden Retriever',
      edad: 3,
      sexo: 'Macho',
      imagen: 'https://images.unsplash.com/photo-1552053831-71594a27632d?w=400',
      pesoKg: 30.5,
      color: 'Dorado',
      fechaIngreso: new Date('2026-01-10'),
      dueno: { id: 1, cedula: '1023456789', nombre: 'Carlos Pérez', correo: 'carlos@email.com', contrasena: 'pass123', celular: '3001234567', mascotas: [] },
      estado: 'Activo',
      tratamientos: [],
    },
    {
      id: 2,
      nombre: 'Luna',
      especie: 'Gato',
      raza: 'Siamés',
      edad: 2,
      sexo: 'Hembra',
      imagen: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=400',
      pesoKg: 4.2,
      color: 'Crema',
      fechaIngreso: new Date('2026-01-15'),
      dueno: { id: 2, cedula: '1098765432', nombre: 'María García', correo: 'maria@email.com', contrasena: 'pass123', celular: '3109876543', mascotas: [] },
      estado: 'Activo',
      tratamientos: [],
    },
    {
      id: 3,
      nombre: 'Rocky',
      especie: 'Perro',
      raza: 'Bulldog Francés',
      edad: 5,
      sexo: 'Macho',
      imagen: 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=400',
      pesoKg: 12.0,
      color: 'Atigrado',
      fechaIngreso: new Date('2026-01-20'),
      dueno: { id: 3, cedula: '1045678901', nombre: 'Juan Rodríguez', correo: 'juan@email.com', contrasena: 'pass123', celular: '3204567890', mascotas: [] },
      estado: 'Activo',
      tratamientos: [],
    },
    {
      id: 4,
      nombre: 'Mia',
      especie: 'Gato',
      raza: 'Persa',
      edad: 1,
      sexo: 'Hembra',
      imagen: 'https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=400',
      pesoKg: 3.8,
      color: 'Blanco',
      fechaIngreso: new Date('2026-02-01'),
      dueno: { id: 4, cedula: '1056789012', nombre: 'Ana Martínez', correo: 'ana@email.com', contrasena: 'pass123', celular: '3015678901', mascotas: [] },
      estado: 'Activo',
      tratamientos: [],
    },
    {
      id: 5,
      nombre: 'Toby',
      especie: 'Perro',
      raza: 'Labrador',
      edad: 7,
      sexo: 'Macho',
      imagen: 'https://images.unsplash.com/photo-1579213838058-4a19dfa4bf5b?w=400',
      pesoKg: 28.0,
      color: 'Negro',
      fechaIngreso: new Date('2026-02-05'),
      dueno: { id: 5, cedula: '1067890123', nombre: 'Luis Hernández', correo: 'luis@email.com', contrasena: 'pass123', celular: '3116789012', mascotas: [] },
      estado: 'Inactivo',
      tratamientos: [],
    },
    {
      id: 6,
      nombre: 'Simba',
      especie: 'Gato',
      raza: 'Maine Coon',
      edad: 4,
      sexo: 'Macho',
      imagen: 'https://images.unsplash.com/photo-1533738363-b7f9aef128ce?w=400',
      pesoKg: 6.5,
      color: 'Naranja',
      fechaIngreso: new Date('2026-02-10'),
      dueno: { id: 1, cedula: '1023456789', nombre: 'Carlos Pérez', correo: 'carlos@email.com', contrasena: 'pass123', celular: '3001234567', mascotas: [] },
      estado: 'Activo',
      tratamientos: [],
    },
    {
      id: 7,
      nombre: 'Coco',
      especie: 'Perro',
      raza: 'Poodle',
      edad: 2,
      sexo: 'Hembra',
      imagen: 'https://images.unsplash.com/photo-1594149825201-57885f740d83?w=400',
      pesoKg: 8.0,
      color: 'Blanco',
      fechaIngreso: new Date('2026-02-15'),
      dueno: { id: 2, cedula: '1098765432', nombre: 'María García', correo: 'maria@email.com', contrasena: 'pass123', celular: '3109876543', mascotas: [] },
      estado: 'Activo',
      tratamientos: [],
    },
    {
      id: 8,
      nombre: 'Nala',
      especie: 'Gato',
      raza: 'Bengalí',
      edad: 3,
      sexo: 'Hembra',
      imagen: 'https://images.unsplash.com/photo-1591871937573-74dbba515c4c?w=400',
      pesoKg: 4.0,
      color: 'Marrón',
      fechaIngreso: new Date('2026-03-01'),
      dueno: { id: 3, cedula: '1045678901', nombre: 'Juan Rodríguez', correo: 'juan@email.com', contrasena: 'pass123', celular: '3204567890', mascotas: [] },
      estado: 'Activo',
      tratamientos: [],
    },
    {
      id: 9,
      nombre: 'Thor',
      especie: 'Perro',
      raza: 'Pastor Alemán',
      edad: 6,
      sexo: 'Macho',
      imagen: 'https://images.unsplash.com/photo-1589941013453-ec89f33b5e95?w=400',
      pesoKg: 35.0,
      color: 'Negro y fuego',
      fechaIngreso: new Date('2026-03-05'),
      dueno: { id: 4, cedula: '1056789012', nombre: 'Ana Martínez', correo: 'ana@email.com', contrasena: 'pass123', celular: '3015678901', mascotas: [] },
      estado: 'Activo',
      tratamientos: [],
    },
    {
      id: 10,
      nombre: 'Cleo',
      especie: 'Gato',
      raza: 'Ragdoll',
      edad: 1,
      sexo: 'Hembra',
      imagen: 'https://images.unsplash.com/photo-1513245543132-31f507417b26?w=400',
      pesoKg: 3.5,
      color: 'Gris',
      fechaIngreso: new Date('2026-03-10'),
      dueno: { id: 5, cedula: '1067890123', nombre: 'Luis Hernández', correo: 'luis@email.com', contrasena: 'pass123', celular: '3116789012', mascotas: [] },
      estado: 'Activo',
      tratamientos: [],
    },
  ];

  private nextId = 11;

  getMascotas(): Mascota[] {
    return this.mascotas.slice();
  }

  getMascotaById(id: number): Mascota | undefined {
    return this.mascotas.find((m) => m.id === id);
  }

  createMascota(mascota: Mascota): Mascota {
    const nuevaMascota: Mascota = Object.assign({}, mascota, {
      id: this.nextId++,
    });
    this.mascotas.push(nuevaMascota);
    return nuevaMascota;
  }

  updateMascota(id: number, mascota: Mascota): Mascota | undefined {
    const index = this.mascotas.findIndex((m) => m.id === id);
    if (index === -1) return undefined;

    this.mascotas[index] = Object.assign({}, mascota, { id });
    return this.mascotas[index];
  }

  obtenerFichaClinica(id: number): FichaClinicaDTO | undefined {
    const mascota = this.mascotas.find((m) => m.id === id);
    if (!mascota) return undefined;

    const veterinario = this.getVeterinarios()[0];
    const propietario = mascota.dueno;

    const historial: TratamientoDetalle[] = mascota.tratamientos.map((t) => {
      return {
        tratamiento: t,
        drogaNombre: 'No registrada',
      };
    });

    return {
      mascota,
      propietario,
      historial,
      veterinarioId: 0,
      veterinario,
    };
  }

  cambiarEstado(id: number, estado: Estado): Mascota | undefined {
    const mascota = this.mascotas.find((m) => m.id === id);
    if (!mascota) return undefined;

    mascota.estado = estado;
    return mascota;
  }

  getMascotasDeDueno(duenoId: number): Mascota[] {
    return this.mascotas.filter((m) => m.dueno?.id === duenoId);
  }

  removeMascotasDeDueno(duenoId: number): void {
    this.mascotas = this.mascotas.filter((m) => m.dueno?.id !== duenoId);
  }

  private veterinarios: Veterinario[] = [
    { id: 1, cedula: '2012345678', contrasena: 'vet123', correo: 'laura@vetopia.com', especialidad: 'Medicina Veterinaria', numeroAtenciones: 150, nombre: 'Dra. Laura Gómez', estado: 'Activo', urlFoto: '', tratamientos: [] },
    { id: 2, cedula: '2023456789', contrasena: 'vet123', correo: 'carlos@vetopia.com', especialidad: 'Veterinario Cirujano', numeroAtenciones: 200, nombre: 'Dr. Carlos Ruiz', estado: 'Activo', urlFoto: '', tratamientos: [] },
    { id: 3, cedula: '2034567890', contrasena: 'vet123', correo: 'felipe@vetopia.com', especialidad: 'Cardiología veterinaria', numeroAtenciones: 80, nombre: 'Dra. Felipe Torres', estado: 'Activo', urlFoto: '', tratamientos: [] },
  ];

  getVeterinarios(): Veterinario[] {
    return this.veterinarios.slice();
  }
}
